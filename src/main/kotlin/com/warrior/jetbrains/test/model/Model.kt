package com.warrior.jetbrains.test.model

import com.warrior.jetbrains.test.presenter.Presenter
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

class Model(private val presenter: Presenter) {

    private val logger: Logger = LogManager.getLogger(javaClass)

    fun getChildren(path: Path): List<Path> {
        logger.debug("getChildren. path: $path")
        if (!Files.isDirectory(path)) return listOf(path)
        return try {
            return Files.newDirectoryStream(path).use { it.filter { !Files.isHidden(it) }.toList() }
        } catch (e: IOException) {
            logger.error(e.message, e)
            emptyList()
        }
    }
}
