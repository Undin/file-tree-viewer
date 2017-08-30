package com.warrior.jetbrains.test

import com.warrior.jetbrains.test.ui.FileViewerPanel
import com.warrior.jetbrains.test.ui.INITIAL_HEIGHT
import com.warrior.jetbrains.test.ui.INITIAL_WIDTH
import java.awt.Dimension
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
        frame.preferredSize = Dimension(INITIAL_WIDTH, INITIAL_HEIGHT)

        frame.pack()
        frame.isVisible = true
    }
}
