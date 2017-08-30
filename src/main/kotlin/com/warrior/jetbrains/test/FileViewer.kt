package com.warrior.jetbrains.test

import com.warrior.jetbrains.test.ui.FileViewerPanel
import java.nio.file.FileSystems
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

        frame.add(FileViewerPanel(FileSystems.getDefault().rootDirectories))

        frame.pack()
        frame.isVisible = true
    }
}
