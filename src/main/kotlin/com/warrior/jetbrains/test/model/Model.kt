package com.warrior.jetbrains.test.model

import com.warrior.jetbrains.test.isZip
import com.warrior.jetbrains.test.presenter.Presenter
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.IOException
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path

class Model(private val presenter: Presenter) {

    private val logger: Logger = LogManager.getLogger(javaClass)

    fun getChildren(path: Path): List<Path> {
        logger.debug("getChildren. path: ${path.toUri()}")
        if (path.isZip) {
            val zipChildren = getZipRootChildren(path)
            if (zipChildren != null) {
                return zipChildren
            }
        }

        if (!Files.isDirectory(path)) return emptyList()
        return try {
            return Files.newDirectoryStream(path).use { it.filter { !Files.isHidden(it) }.toList() }
        } catch (e: IOException) {
            logger.error("Failed to get children of $path", e)
            emptyList()
        }
    }

    private fun getZipRootChildren(zipPath: Path): List<Path>? {
        val zipfs = try {
            FileSystems.newFileSystem(zipPath, null)
        } catch (e: IOException) {
            logger.error("Failed to create new file system for $zipPath")
            return null
        }
        val roots = zipfs.rootDirectories.toList()
        return if (roots.size == 1) getChildren(roots[0]) else roots
    }
}
