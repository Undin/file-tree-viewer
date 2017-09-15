package com.warrior.jetbrains.test.ui.tree

import com.google.common.annotations.VisibleForTesting
import com.google.common.eventbus.Subscribe
import com.warrior.jetbrains.test.event.*
import com.warrior.jetbrains.test.model.FileInfo
import com.warrior.jetbrains.test.model.filter.AnyFileFilter
import com.warrior.jetbrains.test.model.filter.FileFilter
import com.warrior.jetbrains.test.ui.LoadingState
import com.warrior.jetbrains.test.ui.uiAction
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.MutableTreeNode

class FileTreeModel(private val treeRoot: MutableTreeNode) :
        DefaultTreeModel(treeRoot, true) {

    private val logger: Logger = LogManager.getLogger(javaClass)

    private val nodeMapping: MutableMap<FileInfo, FileTreeNode> = HashMap()

    private var filter: FileFilter = AnyFileFilter

    @Subscribe
    fun addRoot(event: AddRootEvent) = uiAction {
        logger.debug("addRoot: $event")
        val count = treeRoot.childCount
        val node = FileTreeNode(event.root)
        nodeMapping[event.root] = node
        insertNodeInto(node, treeRoot, count)
    }

    @Subscribe
    fun onStartLoadingChildren(event: StartLoadingChildrenEvent) = uiAction {
        logger.debug("onStartLoadingChildren: $event")
        if (event.node.state != LoadingState.EMPTY) return@uiAction
        setLoadingState(event.node)
    }

    @Subscribe
    fun setNodeChildren(event: ChildrenLoadedEvent) = uiAction {
        logger.debug("setNodeChildren: $event")
        if (event.node.state != LoadingState.LOADED) {
            setNodeChildren(event.node, event.children)
        }
    }

    @Subscribe
    fun applyFilter(event: ApplyFileFilterEvent) = uiAction {
        logger.debug("applyFilter: $event")
        applyFilter(event.filter)
    }

    fun getNode(file: FileInfo): FileTreeNode? = nodeMapping[file]

    @VisibleForTesting
    fun setLoadingState(node: FileTreeNode) {
        node.setLoadingState().apply(node)
    }

    @VisibleForTesting
    fun setNodeChildren(node: FileTreeNode, children: List<FileInfo>) {
        val childNodes = ArrayList<FileTreeNode>(children.size)
        for (file in children) {
            val childNode = FileTreeNode(file)
            childNodes += childNode
            nodeMapping[file] = childNode
        }

        node.setUnfilteredChildren(childNodes, filter).apply(node)
    }

    @VisibleForTesting
    fun applyFilter(filter: FileFilter) = applyFilter(treeRoot, filter)

    private fun applyFilter(node: MutableTreeNode, filter: FileFilter) {
        this.filter = filter
        if (node is FileTreeNode) {
            node.applyFilter(filter).apply(node)
        }
        for (child in node.children()) {
            child as? MutableTreeNode ?: continue
            applyFilter(child, filter)
        }
    }

    private fun Changes.apply(node: FileTreeNode) {
        val (removedIndexes, removedNodes, insertedIndexes) = this
        if (removedIndexes.isNotEmpty()) {
            nodesWereRemoved(node, removedIndexes, removedNodes)
        }
        if (insertedIndexes.isNotEmpty()) {
            nodesWereInserted(node, insertedIndexes)
        }
    }
}
