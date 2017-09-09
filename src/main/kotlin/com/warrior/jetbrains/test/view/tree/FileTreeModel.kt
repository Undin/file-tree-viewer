package com.warrior.jetbrains.test.view.tree

import com.google.common.annotations.VisibleForTesting
import com.google.common.eventbus.Subscribe
import com.warrior.jetbrains.test.event.*
import com.warrior.jetbrains.test.model.FileInfo
import com.warrior.jetbrains.test.model.filter.AnyFileFilter
import com.warrior.jetbrains.test.model.filter.FileFilter
import com.warrior.jetbrains.test.view.uiAction
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.MutableTreeNode

class FileTreeModel(private val treeRoot: MutableTreeNode) :
        DefaultTreeModel(treeRoot, true) {

    private var filter: FileFilter = AnyFileFilter

    init {
        EventBus.register(this)
    }

    @Subscribe
    fun addRoot(event: AddRootEvent) = uiAction {
        val count = treeRoot.childCount
        insertNodeInto(FileTreeNode(event.root), treeRoot, count)
    }

    @Subscribe
    fun onStartLoadingChildren(event: StartLoadingChildrenEvent) = uiAction { setLoadingState(event.node) }

    @Subscribe
    fun setNodeChildren(event: ChildrenLoadedEvent) = uiAction { setNodeChildren(event.node, event.children) }

    @Subscribe
    fun applyFilter(event: ApplyFileFilterEvent) = uiAction { applyFilter(event.filter) }

    @VisibleForTesting
    fun setLoadingState(node: FileTreeNode) {
        node.setLoadingState().apply(node)
    }

    @VisibleForTesting
    fun setNodeChildren(node: FileTreeNode, children: List<FileInfo>) {
        val nodes = children.map(::FileTreeNode)
        node.setUnfilteredChildren(nodes, filter).apply(node)
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
