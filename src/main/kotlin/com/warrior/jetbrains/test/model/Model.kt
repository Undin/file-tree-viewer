package com.warrior.jetbrains.test.model

import com.warrior.jetbrains.test.model.filetype.NameFileTypeDetector
import org.apache.commons.httpclient.util.URIUtil
import org.apache.commons.vfs2.CacheStrategy
import org.apache.commons.vfs2.FileObject
import org.apache.commons.vfs2.impl.StandardFileSystemManager
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.IOException
import java.net.URI
import java.nio.file.FileSystems
import java.nio.file.Path
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

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

    // TODO: make it asynchronous
    fun createFtpServerRoot(host: String, username: String?, password: CharArray?): FileInfo? {
        logger.debug("createFtpServerRoot. host: $host, username: $username, password: ${Arrays.toString(password)}")
        val host = host.removePrefix("ftp://")
        val uri = createFtpUri(host, username, password) ?: return null
        val file = resolveFile(uri) ?: return null
        return FileInfo(file, host, FileLocation.FTP, fileType(file))
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
