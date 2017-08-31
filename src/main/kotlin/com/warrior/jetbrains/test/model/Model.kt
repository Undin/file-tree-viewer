package com.warrior.jetbrains.test.model

import com.warrior.jetbrains.test.isDirectory
import com.warrior.jetbrains.test.isZip
import com.warrior.jetbrains.test.presenter.Presenter
import org.apache.commons.vfs2.FileObject
import org.apache.commons.vfs2.VFS
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.IOException
import java.net.URI
import java.nio.file.FileSystems

class Model(private val presenter: Presenter) {

    private val logger: Logger = LogManager.getLogger(javaClass)

    fun getLocalRoots(): List<NodeData> {
        logger.debug("getLocalRoots")
        return FileSystems.getDefault()
                .rootDirectories
                .mapNotNull { path ->
                    val file = resolveFile(path.toUri()) ?: return@mapNotNull null
                    val baseName = file.name.baseName
                    val name = if (baseName.isNotEmpty()) baseName else path.toString()
                    NodeData(file, name)
                }
    }

    fun getChildren(file: FileObject): List<NodeData> {
        logger.debug("getChildren. path: $file")
        if (file.isZip) {
            val zipChildren = getArchiveChildren(file)
            if (zipChildren != null) {
                return zipChildren
            }
        }

        if (!file.isDirectory) return emptyList()
        return try {
            file.children.filter { !it.isHidden }.map { NodeData(it, it.name.baseName) }
        } catch (e: IOException) {
            logger.error("Failed to get children of $file", e)
            emptyList()
        }
    }

    private fun getArchiveChildren(zipFile: FileObject): List<NodeData>? {
        val extension = zipFile.name.extension
        check(extension == "zip" || extension == "jar") {
            "Archive file must be zip or jar"
        }
        val archiveFile = resolveFile(URI.create("$extension:$zipFile")) ?: return null
        return getChildren(archiveFile)
    }

    fun createFtpServerRoot(host: String, username: String?, password: CharArray?): NodeData? {
        logger.debug("createFtpServerRoot. host: $host, username: $username, password: $password")
        val uri = createFtpUri(host, username, password) ?: return null
        val file = resolveFile(uri) ?: return null
        return NodeData(file, host)
    }

    private fun resolveFile(uri: URI): FileObject? {
        return try {
            VFS.getManager().resolveFile(uri)
        } catch (e: IOException) {
            logger.error("Failed to resolve $uri", e)
            null
        }
    }

    private fun createFtpUri(host: String, username: String?, password: CharArray?): URI? {
        val stringURI = buildString {
            append("ftp://")
            if (username.isNullOrBlank()) {
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
