package com.warrior.jetbrains.test.view

import com.warrior.jetbrains.test.model.FileInfo
import com.warrior.jetbrains.test.view.filter.FileFilter
import com.warrior.jetbrains.test.presenter.Presenter
import com.warrior.jetbrains.test.view.content.Content
import com.warrior.jetbrains.test.view.content.ContentPanel
import com.warrior.jetbrains.test.view.tree.*
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
    private val treeModel: FileTreeModel = FileTreeModel(treeRoot, true)
    private val tree: JTree = createFileTree(treeModel)
    private val contentPanel: ContentPanel = ContentPanel()

    init {
        tree.cellRenderer = FileTreeCellRender()
        val contentScrollPane = JScrollPane(contentPanel).apply {
            verticalScrollBar.unitIncrement = 16
        }
        val splitView = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, JScrollPane(tree), contentScrollPane).apply {
            dividerLocation = INITIAL_DIVIDER_LOCATION
            dividerSize = DIVIDER_SIZE
        }

        add(splitView)
    }

    fun addRoot(root: FileInfo) {
        val count = treeRoot.childCount
        treeModel.insertNodeInto(FileTreeNode(root), treeRoot, count)
    }

    fun setLoadingState(node: FileTreeNode) {
        treeModel.setLoadingState(node)
    }

    fun setChildren(node: FileTreeNode, children: List<FileTreeNode>) {
        treeModel.setNodeChildren(node, children)
    }

    fun setContent(content: Content) {
        contentPanel.setContent(content)
    }

    fun setContentLoading() {
        contentPanel.setContentLoading()
    }

    override fun valueChanged(e: TreeSelectionEvent) {
        val node = e.newLeadSelectionPath?.lastPathComponent as? FileTreeNode ?: return
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

    fun applyFileFilter(filter: FileFilter) {
        treeModel.applyFilter(filter)
    }

    private fun createFileTree(model: TreeModel): JTree {
        return JTree(model).apply {
            selectionModel.selectionMode = TreeSelectionModel.SINGLE_TREE_SELECTION
            isRootVisible = false
            addTreeSelectionListener(this@FileViewerPanel)
            addTreeWillExpandListener(this@FileViewerPanel)
        }
    }
}
