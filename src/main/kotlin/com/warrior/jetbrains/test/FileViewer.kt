package com.warrior.jetbrains.test

import com.warrior.jetbrains.test.ui.FileViewerFrame
import javax.swing.SwingUtilities
import javax.swing.UIManager

object FileViewer {

    @JvmStatic
    fun main(args: Array<String>) {
        // TODO: find out behaviour on not mac os
        System.setProperty("apple.laf.useScreenMenuBar", "true")
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        SwingUtilities.invokeLater { createUI() }
    }

    private fun createUI() {
        val frame = FileViewerFrame()
        frame.isVisible = true
    }
}
