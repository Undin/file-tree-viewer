package com.warrior.jetbrains.test.ui

import com.warrior.jetbrains.test.presenter.Presenter
import com.warrior.jetbrains.test.presenter.PresenterImpl
import com.warrior.jetbrains.test.view.View
import org.apache.commons.vfs2.FileObject
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.awt.Dimension
import javax.swing.*

class FileViewerFrame : JFrame("FileViewer"), View {

    private val logger: Logger = LogManager.getLogger(javaClass)

    private val presenter: Presenter = PresenterImpl(this)
    private val panel: FileViewerPanel = FileViewerPanel(presenter)

    init {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        add(panel)
        jMenuBar = createMenu()
        preferredSize = Dimension(INITIAL_WIDTH, INITIAL_HEIGHT)
        pack()
        presenter.onStart()
    }

    override fun addRoot(root: FileObject) {
        logger.debug("addRoot: $root")
        SwingUtilities.invokeLater { panel.addRoot(root) }
    }

    override fun setContentData(data: List<FileObject>) {
        logger.debug("setContentData: $data")
        SwingUtilities.invokeLater { panel.setContentData(data) }
    }

    private fun createMenu(): JMenuBar {
        val settingsMenu = JMenu("Settings")
        val ftpMenuItem = JMenuItem("Add new FTP server")
        ftpMenuItem.addActionListener { e ->
            showAddFtpServerDialog()
        }
        settingsMenu.add(ftpMenuItem)
        return JMenuBar().apply { add(settingsMenu) }
    }

    private fun showAddFtpServerDialog() {
        val host = JTextField()
        val username = JTextField()
        val password = JPasswordField()
        val inputs = arrayOf<JComponent>(
                JLabel("Host"), host,
                JLabel("User"), username,
                JLabel("Password"), password
        )
        val result = JOptionPane.showConfirmDialog(null, inputs,
                "Add new FTP server", JOptionPane.OK_CANCEL_OPTION)
        if (result == JOptionPane.OK_OPTION) {
            presenter.onAddNewFtpServer(host.text, username.text, password.password)
        }
    }
}
