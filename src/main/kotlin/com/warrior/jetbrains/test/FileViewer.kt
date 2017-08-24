package com.warrior.jetbrains.test

import java.nio.file.Paths
import javax.swing.JFrame
import javax.swing.SwingUtilities

object FileViewer {

    @JvmStatic
    fun main(args: Array<String>) {
        SwingUtilities.invokeLater { createUI() }
    }

    private fun createUI() {
        val frame = JFrame("FileViewer")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

        val homeDir = System.getProperty("user.home")
        frame.add(FileViewerPanel(Paths.get(homeDir)))

        frame.pack()
        frame.isVisible = true
    }
}
