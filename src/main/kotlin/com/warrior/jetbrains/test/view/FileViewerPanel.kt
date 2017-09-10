package com.warrior.jetbrains.test.view

import com.google.common.eventbus.Subscribe
import com.warrior.jetbrains.test.event.*
import com.warrior.jetbrains.test.view.content.*
import com.warrior.jetbrains.test.view.tree.FileTreeCellRender
import com.warrior.jetbrains.test.view.tree.FileTreeModel
import com.warrior.jetbrains.test.view.tree.FileTreeNode
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.awt.GridLayout
import javax.swing.*
import javax.swing.event.*
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreeModel
import javax.swing.tree.TreeSelectionModel

class FileViewerPanel: JPanel(GridLayout(1, 1)), TreeSelectionListener, TreeWillExpandListener {

    private val logger: Logger = LogManager.getLogger(javaClass)

    private val treeRoot: DefaultMutableTreeNode = DefaultMutableTreeNode()
    private val treeModel: FileTreeModel = FileTreeModel(treeRoot)
    private val tree: JTree = createFileTree(treeModel)
    private val contentPanel: JPanel = JPanel(GridLayout(1, 1))
    private var contentPreview: BasePreviewPanel = EmptyPreviewPanel()

    init {
        tree.cellRenderer = FileTreeCellRender()
        contentPanel.add(contentPreview)
        val contentScrollPane = JScrollPane(contentPanel).apply {
            verticalScrollBar.unitIncrement = 16
        }
        val splitView = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, JScrollPane(tree), contentScrollPane).apply {
            dividerLocation = UISizes.initialDividerLocation
            dividerSize = UISizes.dividerSize
        }

        add(splitView)

        EventBus.register(treeModel)
        EventBus.register(contentPreview)
    }

    @Subscribe
    fun displayContent(event: DisplayContentEvent) = uiAction {
        logger.debug("displayContent: $event")
        val (content, filter) = event
        val previewPanel: BasePreviewPanel = when (content) {
            is Empty -> EmptyPreviewPanel()
            is FileList -> FolderPreviewPanel(content.files, filter)
            is SingleFile -> FilePreviewPanel(content.file)
        }
        updateContentPanel(previewPanel)
    }

    @Subscribe
    fun onStartLoadingContent(event: StartLoadingContentEvent) = uiAction {
        logger.debug("onStartLoadingContent: $event")
        updateContentPanel(LoadingPreviewPanel())
    }

    override fun valueChanged(e: TreeSelectionEvent) {
        NodeSelectedEvent(e.newLeadSelectionPath?.lastPathComponent as? FileTreeNode).post()
    }

    override fun treeWillExpand(event: TreeExpansionEvent) {
        val node = event.path.lastPathComponent as? FileTreeNode ?: return
        PreNodeExpandEvent(node).post()
    }

    override fun treeWillCollapse(event: TreeExpansionEvent) {}

    private fun updateContentPanel(previewPanel: BasePreviewPanel) {
        EventBus.unregister(contentPreview)
        EventBus.register(previewPanel)

        contentPreview = previewPanel
        contentPanel.removeAll()
        contentPanel.add(previewPanel)
        contentPanel.revalidate()
        contentPanel.repaint()
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
