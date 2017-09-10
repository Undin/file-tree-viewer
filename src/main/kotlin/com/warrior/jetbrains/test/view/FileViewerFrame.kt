package com.warrior.jetbrains.test.view

import com.warrior.jetbrains.test.event.*
import com.warrior.jetbrains.test.view.dialog.NewFTPServerDialog
import java.awt.Dimension
import javax.swing.*

class FileViewerFrame : JFrame("FileViewer") {

    private val panel: FileViewerPanel = FileViewerPanel()

    private var currentExtension: String = ""

    init {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        add(panel)
        jMenuBar = createMenu()
        preferredSize = Dimension(INITIAL_WIDTH, INITIAL_HEIGHT)
        pack()

        EventBus.register(panel)
        StartEvent.post()
    }

    private fun createMenu(): JMenuBar {
        val settingsMenu = JMenu("Settings")

        val ftpMenuItem = JMenuItem("Add new FTP server")
        ftpMenuItem.addActionListener { showAddFtpServerDialog() }
        settingsMenu.add(ftpMenuItem)

        val filterMenuItem = JMenuItem("Set file filter")
        filterMenuItem.addActionListener { showAddFileFilterDialog() }
        settingsMenu.add(filterMenuItem)

        return JMenuBar().apply { add(settingsMenu) }
    }

    private fun showAddFtpServerDialog() {
        val dialog = NewFTPServerDialog(this)
        EventBus.register(dialog)
        dialog.isVisible = true
        EventBus.unregister(dialog)
    }

    private fun showAddFileFilterDialog() {
        val result = JOptionPane.showInputDialog("Input file extension", currentExtension) ?: return
        currentExtension = result
        SetFileFilterEvent(result).post()
    }
}
