package com.warrior.jetbrains.test.model

import com.warrior.jetbrains.test.model.filetype.NameFileTypeDetector
import com.warrior.jetbrains.test.ui.IMAGE_PREVIEW_SIZE
import com.warrior.jetbrains.test.ui.TEXT_PREVIEW_LINES
import org.apache.commons.httpclient.util.URIUtil
import org.apache.commons.vfs2.CacheStrategy
import org.apache.commons.vfs2.FileObject
import org.apache.commons.vfs2.impl.StandardFileSystemManager
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.awt.Image
import java.io.File
import java.io.IOException
import java.net.URI
import java.nio.file.FileSystems
import java.nio.file.Path
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import javax.imageio.ImageIO
import javax.swing.Icon
import javax.swing.ImageIcon

class Model {

    private val logger: Logger = LogManager.getLogger(javaClass)

    private val fileSystemManager = StandardFileSystemManager()
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        fileSystemManager.cacheStrategy = CacheStrategy.MANUAL
        fileSystemManager.init()
    }

    fun getLocalFile(path: Path): FileInfo? {
        val fileObject = resolveFile(path.toUri()) ?: return null
        val baseName = fileObject.name.baseName
        val name = if (baseName.isNotEmpty()) baseName else path.toString()
        val type = fileType(fileObject)
        return FileInfo(fileObject, name, FileLocation.LOCAL, type)
    }

    fun getLocalRoots(): List<FileInfo> {
        logger.debug("getLocalRoots")
        return FileSystems.getDefault()
                .rootDirectories
                .mapNotNull { getLocalFile(it) }
                .sortedBy { it.name } //+ listOfNotNull(createFtpServerRoot("ftp.lip6.fr", "", CharArray(0)))
    }

    fun getChildrenAsync(fileInfo: FileInfo, onSuccess: (List<FileInfo>) -> Unit): Future<*> {
        logger.debug("getChildrenAsync. path: $fileInfo")
        return executor.submit {
            logger.debug("runAsync: $fileInfo")
            val children = getChildren(fileInfo)
//            Thread.sleep(2000)
            onSuccess(children)
        }
    }

    fun createFtpServerRoot(host: String, username: String?, password: CharArray?): FileInfo? {
        logger.debug("createFtpServerRoot. host: $host, username: $username, password: ${Arrays.toString(password)}")
        val host = host.removePrefix("ftp://")
        val uri = createFtpUri(host, username, password) ?: return null
        val file = resolveFile(uri) ?: return null
        return FileInfo(file, host, FileLocation.FTP, fileType(file))
    }

    fun loadImage(fileInfo: FileInfo, callback: (Icon?) -> Unit): Future<*> {
        logger.debug("loadImage: ${fileInfo.file}")
        return executor.submit {
            val icon = try {
                val image = ImageIO.read(File(fileInfo.path))
                val maxSize = maxOf(image.width, image.height)
                if (maxSize <= IMAGE_PREVIEW_SIZE) {
                    ImageIcon(image)
                } else {
                    val scale = maxOf(image.width, image.height) / IMAGE_PREVIEW_SIZE.toDouble()
                    val scaledWidth = (image.width / scale).toInt()
                    val scaledHeight = (image.height / scale).toInt()
                    val scaledImage = image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH)
                    ImageIcon(scaledImage)
                }
            } catch (e: IOException) {
                logger.error("Failed to load image ${fileInfo.file}", e)
                null
            }
            callback(icon)
        }
    }

    fun loadText(fileInfo: FileInfo, callback: (String?) -> Unit): Future<*> {
        logger.debug("loadImage: ${fileInfo.file}")
        return executor.submit {
            val text = try {
                File(fileInfo.path).useLines { lines ->
                    val iterator = lines.iterator()
                    var linesCount = 0
                    val builder = StringBuilder()
                    while (iterator.hasNext() && linesCount < TEXT_PREVIEW_LINES) {
                        builder.append(iterator.next()).append("\n")
                        linesCount++
                    }
                    if (iterator.hasNext()) {
                        builder.append("...")
                    }
                    builder.toString()
                }
            } catch (e: IOException) {
                logger.debug("Failed to load text from ${fileInfo.file}", e)
                null
            }
            callback(text)
        }
    }

    private fun getChildren(fileInfo: FileInfo): List<FileInfo> {
        if (fileInfo.isArchive && fileInfo.isLocal) {
            val zipChildren = getArchiveChildren(fileInfo)
            if (zipChildren != null) {
                return zipChildren
            }
        }

        if (!fileInfo.isFolder) return emptyList()
        return try {
            fileInfo.file
                    .children
                    .filter { !it.isHidden }
                    .map { FileInfo(it, it.name.baseName, fileInfo.location, fileType(it)) }
                    .sortedBy { it.name }
        } catch (e: IOException) {
            logger.error("Failed to get children of $fileInfo", e)
            emptyList()
        }
    }

    private fun getArchiveChildren(fileInfo: FileInfo): List<FileInfo>? {
        check(fileInfo.isLocal && fileInfo.isArchive) {
            "Archive file must be local archive"
        }
        val extension = fileInfo.file.name.extension
        val archiveRoot = resolveFile(URI.create("$extension:${fileInfo.file}!")) ?: return null
        val archive = FileInfo(archiveRoot, "", FileLocation.ARCHIVE, fileType(archiveRoot))
        return getChildren(archive)
    }

    private fun resolveFile(uri: URI): FileObject? {
        return try {
            fileSystemManager.resolveFile(uri)
        } catch (e: IOException) {
            logger.error("Failed to resolve $uri", e)
            null
        }
    }

    private fun fileType(file: FileObject): FileType {
        if (file.isDirectory) return FileType.FOLDER
        val detector = NameFileTypeDetector()
        return detector.fileType(file.name.baseName)
    }

    private fun createFtpUri(host: String, username: String?, password: CharArray?): URI? {
        val stringURI = buildString {
            append("ftp://")
            if (!username.isNullOrEmpty()) {
                // '!!' is safe here because of previous check
                append(username!!.escaped)
                if (password != null && password.isNotEmpty()) {
                    append(":")
                    append(password)
                }
            }
            if (length > "ftp://".length) {
                append("@")
            }
            append(host)
        }
        return try {
            URI.create(stringURI)
        } catch (e: Exception) {
            logger.error("Invalid uri: $stringURI", e)
            null
        }
    }

    private val String.escaped get() = URIUtil.encodeAll(this)
}
