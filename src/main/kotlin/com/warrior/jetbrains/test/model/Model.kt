package com.warrior.jetbrains.test.model

import com.warrior.jetbrains.test.isDirectory
import com.warrior.jetbrains.test.isZip
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
        return FileInfo(fileObject, name)
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
            val children = getChildren(fileInfo.file)
//            Thread.sleep(2000)
            onSuccess(children)
        }
    }

    fun createFtpServerRoot(host: String, username: String?, password: CharArray?): FileInfo? {
        logger.debug("createFtpServerRoot. host: $host, username: $username, password: $password")
        val uri = createFtpUri(host, username, password) ?: return null
        val file = resolveFile(uri) ?: return null
        return FileInfo(file, host)
    }

    private fun getChildren(file: FileObject): List<FileInfo> {
        if (file.isZip) {
            val zipChildren = getArchiveChildren(file)
            if (zipChildren != null) {
                return zipChildren
            }
        }

        if (!file.isDirectory) return emptyList()
        return try {
            file.children
                    .filter { !it.isHidden }
                    .map { FileInfo(it, it.name.baseName) }
                    .sortedBy { it.name }
        } catch (e: IOException) {
            logger.error("Failed to get children of $file", e)
            emptyList()
        }
    }

    private fun getArchiveChildren(archive: FileObject): List<FileInfo>? {
        val extension = archive.name.extension
        check(extension == "zip" || extension == "jar") {
            "Archive file must be zip or jar"
        }
        val archiveFile = resolveFile(URI.create("$extension:$archive!")) ?: return null
        return getChildren(archiveFile)
    }

    private fun resolveFile(uri: URI): FileObject? {
        return try {
            fileSystemManager.resolveFile(uri)
        } catch (e: IOException) {
            logger.error("Failed to resolve $uri", e)
            null
        }
    }

    private fun createFtpUri(host: String, username: String?, password: CharArray?): URI? {
        val stringURI = buildString {
            append("ftp://")
            if (!username.isNullOrEmpty()) {
                append(username)
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
}
