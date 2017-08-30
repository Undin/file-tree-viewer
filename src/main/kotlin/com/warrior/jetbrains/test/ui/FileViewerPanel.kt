package com.warrior.jetbrains.test.ui

import com.warrior.jetbrains.test.presenter.Presenter
import com.warrior.jetbrains.test.presenter.PresenterImpl
import com.warrior.jetbrains.test.tree.FileNodeData
import com.warrior.jetbrains.test.tree.FileTreeNode
import com.warrior.jetbrains.test.view.View
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.awt.GridLayout
import java.nio.file.Path
import javax.swing.*
import javax.swing.event.*
import javax.swing.tree.*

class FileViewerPanel(
        rootPaths: Iterable<Path>
) : JPanel(GridLayout(1, 1)),
    View,
    TreeSelectionListener,
    TreeWillExpandListener {

    private val logger: Logger = LogManager.getLogger(javaClass)

    private val presenter: Presenter = PresenterImpl(this)

    private val root: DefaultMutableTreeNode
    private val tree: JTree
    private val content: ContentPanel = ContentPanel()

    init {
        root = DefaultMutableTreeNode()
        for (path in rootPaths) {
            root.add(node(path))
        }

        tree = createFileTree(DefaultTreeModel(root, true))

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

    override fun valueChanged(e: TreeSelectionEvent) {
        val node = e.newLeadSelectionPath.lastPathComponent as? FileTreeNode ?: return
        presenter.onNodeSelected(node)
    }

    override fun treeWillExpand(event: TreeExpansionEvent) {
        val node = event.path.lastPathComponent as? FileTreeNode ?: return
        presenter.onPreNodeExpand(node)
    }

    override fun treeWillCollapse(event: TreeExpansionEvent) {
        val node = event.path.lastPathComponent as? FileTreeNode ?: return
        presenter.onPreNodeCollapse(node)
    }

    private fun createFileTree(model: TreeModel): JTree {
        return JTree(model).apply {
            selectionModel.selectionMode = TreeSelectionModel.SINGLE_TREE_SELECTION
            isRootVisible = false
            addTreeSelectionListener(this@FileViewerPanel)
            addTreeWillExpandListener(this@FileViewerPanel)
        }
    }

    private fun node(path: Path): FileTreeNode = FileTreeNode(FileNodeData(path))
}
