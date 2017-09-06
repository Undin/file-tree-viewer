package com.warrior.jetbrains.test.view.tree

import com.warrior.jetbrains.test.model.filter.AnyFileFilter
import com.warrior.jetbrains.test.model.filter.FileFilter
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.MutableTreeNode

class FileTreeModel(private val treeRoot: MutableTreeNode) :
        DefaultTreeModel(treeRoot, true) {

    private var filter: FileFilter = AnyFileFilter

    fun setLoadingState(node: FileTreeNode) {
        node.setLoadingState().apply(node)
    }

    fun setNodeChildren(node: FileTreeNode, children: List<FileTreeNode>) {
        node.setUnfilteredChildren(children, filter).apply(node)
    }

    fun applyFilter(filter: FileFilter) {
        applyFilter(treeRoot, filter)
    }

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
