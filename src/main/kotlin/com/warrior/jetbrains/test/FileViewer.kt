package com.warrior.jetbrains.test

import com.warrior.jetbrains.test.event.EventBus
import com.warrior.jetbrains.test.model.Model
import com.warrior.jetbrains.test.model.ModelImpl
import com.warrior.jetbrains.test.view.FileViewerFrame
import com.warrior.jetbrains.test.view.uiAction
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import javax.swing.UIManager

object FileViewer {

    private val logger: Logger = LogManager.getLogger(javaClass)
    private val model: Model = ModelImpl()

    @JvmStatic
    fun main(args: Array<String>) {
        EventBus.register(model)
        setSystemLookAndFeel()
        uiAction { createUI() }
    }

    private fun setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        } catch (e: Exception) {
            logger.error("Can't set system LnF", e)
        }
        if (SystemInfo.isMac) {
            System.setProperty("apple.laf.useScreenMenuBar", "true")
        }
    }

    private fun createUI() {
        val frame = FileViewerFrame()
        frame.isVisible = true
    }
}
