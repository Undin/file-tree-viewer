package com.warrior.jetbrains.test.presenter

import com.warrior.jetbrains.test.view.View
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

class Presenter(private val view: View) {

    private val logger: Logger = LogManager.getLogger(javaClass)

    fun onItemSelected(path: Path) {
        if (Files.isDirectory(path)) {
            try {
                // TODO: do it asynchronously
                val children = Files.newDirectoryStream(path).use { it.toList() }
                view.setContentData(children)
            } catch (e: IOException) {
                logger.error(e.message, e)
            }
        } else {
            view.setContentData(listOf(path))
        }
    }
}
