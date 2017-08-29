package com.warrior.jetbrains.test.ui

import com.warrior.jetbrains.test.presenter.Presenter
import com.warrior.jetbrains.test.tree.FileNodeData
import com.warrior.jetbrains.test.tree.FileTreeNode
import com.warrior.jetbrains.test.view.View
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.awt.GridLayout
import java.nio.file.Path
import javax.swing.*
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeSelectionModel

class FileViewerPanel(path: Path) : JPanel(GridLayout(1, 1)), View {

    private val logger: Logger = LogManager.getLogger(javaClass)

    private val presenter: Presenter = Presenter(this)

    private val root: FileTreeNode = node(path)
    private val tree = JTree(DefaultTreeModel(root, true))
    private val content: ContentPanel = ContentPanel()

    init {
        tree.selectionModel.selectionMode = TreeSelectionModel.SINGLE_TREE_SELECTION
        tree.addTreeSelectionListener { e ->
            val selectedNode = e.newLeadSelectionPath.lastPathComponent as? FileTreeNode
                    ?: return@addTreeSelectionListener
            presenter.onItemSelected(selectedNode.userObject.path)
        }
        val contentScrollPane = JScrollPane(content).apply {
            verticalScrollBar.unitIncrement = 16
        }
        val splitView = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, JScrollPane(tree), contentScrollPane)

        add(splitView)
    }

    override fun setContentData(data: List<Path>) {
        logger.debug("setContentData: " + data)
        content.setContent(data)
    }

    private fun node(path: Path): FileTreeNode = FileTreeNode(FileNodeData(path))
}
