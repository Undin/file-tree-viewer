package com.warrior.jetbrains.test

import com.warrior.jetbrains.test.event.EventBus
import com.warrior.jetbrains.test.model.Model
import com.warrior.jetbrains.test.model.ModelImpl
import com.warrior.jetbrains.test.view.FileViewerFrame
import com.warrior.jetbrains.test.view.uiAction
import javax.swing.UIManager

object FileViewer {

    private val model: Model = ModelImpl()

    @JvmStatic
    fun main(args: Array<String>) {
        EventBus.register(model)

        // TODO: find out behaviour on not mac os
        System.setProperty("apple.laf.useScreenMenuBar", "true")
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        uiAction { createUI() }
    }

    private fun createUI() {
        val frame = FileViewerFrame()
        frame.isVisible = true
    }
}
