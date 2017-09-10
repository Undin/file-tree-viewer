package com.warrior.jetbrains.test.ui

import com.google.common.eventbus.Subscribe
import com.warrior.jetbrains.test.event.*
import com.warrior.jetbrains.test.model.FileLocation
import com.warrior.jetbrains.test.ui.content.*
import com.warrior.jetbrains.test.ui.tree.FileTreeCellRender
import com.warrior.jetbrains.test.ui.tree.FileTreeModel
import com.warrior.jetbrains.test.ui.tree.FileTreeNode
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.awt.GridLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
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
        tree.addMouseListener(TreePopupAdapter(this::showContentMenu))
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

    @Subscribe
    fun updateContentData(event: ContentDataLoadedEvent) = uiAction {
        logger.debug("updateContentData: $event")
        contentPreview.updateContentData(event.data)
    }

    @Subscribe
    fun applyFileFilter(event: ApplyFileFilterEvent) = uiAction {
        logger.debug("applyFileFilter: $event")
        contentPreview.applyFileFilter(event.filter)
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

    private fun showContentMenu(e: MouseEvent) {
        val node = tree.getPathForLocation(e.x, e.y)?.lastPathComponent as? FileTreeNode ?: return
        val file = node.userObject
        // We want to allow remove only ftp servers
        if (file.isRoot && file.location == FileLocation.FTP) {
            val popup = JPopupMenu()
            val removeItem = JMenuItem("Remove")
            removeItem.addActionListener {
                treeModel.removeNodeFromParent(node)
                // notify model to cancel all tasks of all inherited files
                RootRemoved(file).post()
            }
            popup.add(removeItem)
            popup.show(tree, e.x, e.y)
        }
    }

    private class TreePopupAdapter(private val action: (MouseEvent) -> Unit) : MouseAdapter() {
        override fun mouseReleased(e: MouseEvent) {
            if (e.isPopupTrigger) {
                action(e)
            }
        }

        override fun mousePressed(e: MouseEvent) {
            if (e.isPopupTrigger) {
                action(e)
            }
        }
    }
}
