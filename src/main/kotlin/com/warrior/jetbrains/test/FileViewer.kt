package com.warrior.jetbrains.test

import com.warrior.jetbrains.test.ui.FileViewerPanel
import com.warrior.jetbrains.test.ui.INITIAL_HEIGHT
import com.warrior.jetbrains.test.ui.INITIAL_WIDTH
import org.apache.commons.vfs2.FileObject
import org.apache.commons.vfs2.VFS
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

        frame.add(FileViewerPanel(getLocalRoots()))
        frame.preferredSize = Dimension(INITIAL_WIDTH, INITIAL_HEIGHT)

        frame.pack()
        frame.isVisible = true
    }

    private fun getLocalRoots(): List<FileObject> {
        val fileSystemManager = VFS.getManager()
        return FileSystems.getDefault()
                .rootDirectories
                .map { fileSystemManager.resolveFile(it.toUri()) }
    }
}
