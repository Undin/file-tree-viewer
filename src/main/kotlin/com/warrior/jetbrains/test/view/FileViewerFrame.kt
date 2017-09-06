package com.warrior.jetbrains.test.view

import com.warrior.jetbrains.test.view.filter.AnyFileFilter
import com.warrior.jetbrains.test.view.filter.ExtensionFileFilter
import com.warrior.jetbrains.test.model.FileInfo
import com.warrior.jetbrains.test.presenter.Presenter
import com.warrior.jetbrains.test.presenter.PresenterImpl
import com.warrior.jetbrains.test.view.content.Content
import com.warrior.jetbrains.test.view.content.ContentData
import com.warrior.jetbrains.test.view.tree.FileTreeNode
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.awt.Dimension
import javax.swing.*

class FileViewerFrame : JFrame("FileViewer"), View {

    private val logger: Logger = LogManager.getLogger(javaClass)

    private val presenter: Presenter = PresenterImpl(this)
    private val panel: FileViewerPanel = FileViewerPanel(presenter)

    private var currentExtension: String? = null

    init {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        add(panel)
        jMenuBar = createMenu()
        preferredSize = Dimension(INITIAL_WIDTH, INITIAL_HEIGHT)
        pack()
        presenter.onStart()
    }

    override fun addRoot(root: FileInfo) {
        logger.debug("addRoot: $root")
        SwingUtilities.invokeLater { panel.addRoot(root) }
    }

    override fun onStartLoadingChildren(node: FileTreeNode) {
        logger.debug("setContentLoading: $node")
        SwingUtilities.invokeLater { panel.setLoadingState(node) }
    }

    override fun onChildrenLoaded(node: FileTreeNode, children: List<FileInfo>) {
        logger.debug("onChildrenLoaded: $node, $children")
        SwingUtilities.invokeLater { panel.setChildren(node, children) }
    }

    override fun onStartLoadingContent() {
        logger.debug("onStartLoadingContent")
        SwingUtilities.invokeLater { panel.setContentLoading() }
    }

    override fun onContentLoaded(content: Content) {
        logger.debug("onContentLoaded: $content")
        SwingUtilities.invokeLater { panel.setContent(content) }
    }

    override fun onContentDataLoaded(data: ContentData) {
        logger.debug("onContentDataLoaded: $data")
        SwingUtilities.invokeLater { panel.updateContentData(data) }
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

    private fun showAddFileFilterDialog() {
        val result = JOptionPane.showInputDialog("Input file extension", currentExtension) ?: return
        currentExtension = result
        val filter = if (result.isEmpty()) AnyFileFilter else ExtensionFileFilter(result)
        panel.applyFileFilter(filter)
    }
}
