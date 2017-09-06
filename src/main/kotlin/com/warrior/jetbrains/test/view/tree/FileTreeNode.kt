package com.warrior.jetbrains.test.view.tree

import com.warrior.jetbrains.test.model.FileInfo
import com.warrior.jetbrains.test.model.canHaveChildren
import com.warrior.jetbrains.test.model.filter.FileFilter
import com.warrior.jetbrains.test.view.LoadingState
import java.util.*
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreeNode
import kotlin.collections.ArrayList

class FileTreeNode(data: FileInfo) : DefaultMutableTreeNode(data) {

    var state: LoadingState = LoadingState.EMPTY
        private set

    // Unfiltered list of children to support fast filtering.
    private var unfilteredChildren: List<FileTreeNode> = emptyList()
    // Current children of node (after filter applying)
    private var filteredChildren: List<FileTreeNode> = emptyList()
    private var loadingNode: LoadingNode? = null

    override fun getUserObject(): FileInfo = super.getUserObject() as FileInfo
    override fun setUserObject(userObject: Any?) {
        if (userObject !is FileInfo?) error("userObject must be `NodeData`")
        super.setUserObject(userObject)
    }

    override fun getAllowsChildren(): Boolean = getUserObject().canHaveChildren

    override fun getChildCount(): Int {
        return when (state) {
            LoadingState.EMPTY -> 0
            LoadingState.LOADING -> 1
            LoadingState.LOADED -> filteredChildren.size
        }
    }

    override fun getChildAt(index: Int): TreeNode {
        return when (state) {
            LoadingState.EMPTY -> throw ArrayIndexOutOfBoundsException("node has no children")
            LoadingState.LOADING -> {
                if (index != 0) throw ArrayIndexOutOfBoundsException("node has no children")
                loadingNode!!
            }
            LoadingState.LOADED -> filteredChildren[index]
        }
    }

    override fun getIndex(aChild: TreeNode?): Int {
        if (aChild == null) throw IllegalArgumentException("argument is null")
        return if (!isNodeChild(aChild)) -1 else filteredChildren.indexOf(aChild)
    }

    override fun children(): Enumeration<DefaultMutableTreeNode> {
        return when (state) {
            LoadingState.EMPTY -> Collections.emptyEnumeration()
            LoadingState.LOADING -> Collections.enumeration(listOf(loadingNode!!))
            LoadingState.LOADED -> Collections.enumeration(filteredChildren)
        }
    }

    fun setLoadingState(): Changes {
        loadingNode = LoadingNode().apply { setParent(this) }
        state = LoadingState.LOADING
        return Changes(intArrayOf(), emptyArray(), intArrayOf(0))
    }

    fun setUnfilteredChildren(children: List<FileTreeNode>, filter: FileFilter): Changes {
        children.forEach { it.parent = this }
        unfilteredChildren = children
        filteredChildren = children.filter { filter(it) }
        state = LoadingState.LOADED
        val removedNode = loadingNode
        return if (removedNode != null) {
            Changes(intArrayOf(0), arrayOf(removedNode), IntArray(filteredChildren.size) { it })
        } else {
            Changes(intArrayOf(), emptyArray(), IntArray(filteredChildren.size) { it })
        }
    }

    /**
     * Apply filter to current node (not recursively).
     *
     * Returns removed nodes with their previous indexes and indexes of inserted nodes
     * to allow tree model correctly notify listeners about changes.
     */
    fun applyFilter(filter: FileFilter): Changes {
        return when (state) {
            LoadingState.EMPTY, LoadingState.LOADING -> Changes.empty()
            LoadingState.LOADED -> {
                // Break filter applying into two steps:
                // 1. remove unsuitable nodes from current list
                // 2. insert other suitable nodes from unfilteredChildren list

                // Removing
                val afterRemove = ArrayList<FileTreeNode>()
                val removedIndexes = ArrayList<Int>()
                val removedNodes = ArrayList<FileTreeNode>()
                for ((index, child) in filteredChildren.withIndex()) {
                    if (!filter(child)) {
                        removedIndexes += index
                        removedNodes += child
                    } else {
                        afterRemove += child
                    }
                }

                // Inserting
                val insertedIndexes = ArrayList<Int>()
                val afterInsert = ArrayList<FileTreeNode>()
                var index = 0
                for (child in unfilteredChildren) {
                    if (filter(child)) {
                        afterInsert += child
                        if (child == afterRemove.getOrNull(index)) {
                            index++
                        } else {
                            insertedIndexes += afterInsert.size - 1
                        }
                    }
                }

                filteredChildren = afterInsert

                Changes(removedIndexes.toIntArray(),
                        removedNodes.toTypedArray(),
                        insertedIndexes.toIntArray())

            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FileTreeNode

        if (getUserObject() != other.getUserObject()) return false

        return true
    }

    override fun hashCode(): Int = getUserObject().hashCode()

    private operator fun FileFilter.invoke(file: FileTreeNode) = accept(file.getUserObject())
}
