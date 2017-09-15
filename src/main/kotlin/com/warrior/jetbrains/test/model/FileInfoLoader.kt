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
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

object FileInfoLoader {

    const val INVALID_URI = "Can't create valid FTP uri"
    const val RESOLVE_FAILED = "Can't connect to FTP server"

    private val logger: Logger = LogManager.getLogger(javaClass)

    private val fileSystemManager = StandardFileSystemManager()
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        fileSystemManager.cacheStrategy = CacheStrategy.MANUAL
        fileSystemManager.init()
    }

    fun getLocalFile(path: Path, isRoot: Boolean = false): FileInfo? {
        val fileObject = resolveFile(path.toUri()) ?: return null
        val baseName = fileObject.name.baseName
        val name = if (baseName.isNotEmpty()) baseName else path.toString()
        val type = fileType(fileObject)
        return FileInfo(fileObject, name, FileLocation.LOCAL, type, isRoot)
    }

    fun getLocalRoots(): List<FileInfo> {
        logger.debug("getLocalRoots")
        return FileSystems.getDefault()
                .rootDirectories
                .mapNotNull { getLocalFile(it, true) }
                .sortedBy { it.name } //+ listOfNotNull(resolveFtpServer("ftp.lip6.fr", "", CharArray(0)))
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

    fun resolveFtpServerAsync(host: String, username: String?, password: CharArray?, name: String?,
                              callback: (Result<FileInfo, String>) -> Unit): Future<*> {
        logger.debug("resolveFtpServerAsync. host: $host")
        return executor.submit {
            val result = resolveFtpServer(host, username, password, name)
            callback(result)
        }
    }

    private fun resolveFtpServer(host: String, username: String?, password: CharArray?, name: String?): Result<FileInfo, String> {
        val host = host.removePrefix("ftp://")
        return createFtpUri(host, username, password).andThen { uri ->
            val file = resolveFile(uri) ?: return@andThen Err(RESOLVE_FAILED)
            val serverName = if (name.isNullOrEmpty()) host else name!! // !! is safe here because of check
            Ok(FileInfo(file, serverName, FileLocation.FTP, fileType(file), true))
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
                    .map { FileInfo(it, it.name.baseName, fileInfo.location, fileType(it), false) }
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
        val archive = FileInfo(archiveRoot, "", FileLocation.ARCHIVE, fileType(archiveRoot), false)
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

    private fun createFtpUri(host: String, username: String?, password: CharArray?): Result<URI, String> {
        if (host.isBlank()) return Err(INVALID_URI)
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
            Ok(URI.create(stringURI))
        } catch (e: Exception) {
            logger.error("Invalid uri: $stringURI", e)
            Err(INVALID_URI)
        }
    }

    private val String.escaped get() = URIUtil.encodeAll(this)

    private val FileObject.isDirectory get(): Boolean = try {
        isFolder
    } catch (e: IOException) {
        logger.error("Failed to check if file is folder", e)
        false
    }
}
