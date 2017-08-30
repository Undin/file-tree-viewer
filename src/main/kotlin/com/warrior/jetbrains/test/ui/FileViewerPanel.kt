package com.warrior.jetbrains.test.ui

import com.warrior.jetbrains.test.presenter.Presenter
import com.warrior.jetbrains.test.tree.FileNodeData
import com.warrior.jetbrains.test.tree.FileTreeNode
import org.apache.commons.vfs2.FileObject
import java.awt.GridLayout
import javax.swing.*
import javax.swing.event.*
import javax.swing.tree.*

class FileViewerPanel(
        private val presenter: Presenter
) : JPanel(GridLayout(1, 1)),
    TreeSelectionListener,
    TreeWillExpandListener {

    private val treeRoot: DefaultMutableTreeNode = DefaultMutableTreeNode()
    private val model: DefaultTreeModel = DefaultTreeModel(treeRoot, true)
    private val tree: JTree = createFileTree(model)
    private val content: ContentPanel = ContentPanel()

    init {
        val contentScrollPane = JScrollPane(content).apply {
            verticalScrollBar.unitIncrement = 16
        }
        val splitView = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, JScrollPane(tree), contentScrollPane).apply {
            dividerLocation = INITIAL_DIVIDER_LOCATION
            dividerSize = DIVIDER_SIZE
        }

        add(splitView)
    }

    fun addRoot(root: FileObject) {
        val count = treeRoot.childCount
        model.insertNodeInto(node(root), treeRoot, count)
    }

    fun setContentData(data: List<FileObject>) {
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

    private fun node(file: FileObject): FileTreeNode = FileTreeNode(FileNodeData(file))
}
