package com.warrior.jetbrains.test.ui.tree

import com.warrior.jetbrains.test.isDirectory
import com.warrior.jetbrains.test.isZip
import com.warrior.jetbrains.test.model.NodeData
import java.util.*
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.MutableTreeNode
import javax.swing.tree.TreeNode
import kotlin.collections.HashMap

class FileTreeNode(data: NodeData) : DefaultMutableTreeNode(data) {

    private var childPaths: List<NodeData> = emptyList()
    private val childNodes: MutableMap<NodeData, FileTreeNode> = HashMap()

    fun updateChildren(paths: List<NodeData>) {
        childPaths = paths
        childNodes.clear()
    }

    override fun getUserObject(): NodeData = super.getUserObject() as NodeData
    override fun setUserObject(userObject: Any?) {
        if (userObject !is NodeData?) error("userObject must be `NodeData`")
        super.setUserObject(userObject)
    }

    override fun getAllowsChildren(): Boolean {
        val file = getUserObject().file
        return file.isDirectory || file.isZip
    }

    override fun children(): Enumeration<*> = childPaths.asSequence()
            .map { getChild(it) }
            .iterator()
            .asEnumeration()

    override fun insert(newChild: MutableTreeNode?, childIndex: Int) {
        throw UnsupportedOperationException()
    }

    override fun remove(childIndex: Int) {
        throw UnsupportedOperationException()
    }

    override fun add(newChild: MutableTreeNode?) {
        throw UnsupportedOperationException()
    }

    override fun getChildCount(): Int = childPaths.size

    override fun getChildAt(index: Int): TreeNode {
        val path = childPaths[index]
        return getChild(path)
    }

    override fun getIndex(aChild: TreeNode?): Int {
        if (aChild == null) throw IllegalArgumentException("argument is null")
        if (aChild !is FileTreeNode || !isNodeChild(aChild)) return -1
        val data = aChild.getUserObject()
        return childPaths.indexOf(data)
    }

    private fun getChild(data: NodeData): FileTreeNode = childNodes.getOrPut(data) {
        FileTreeNode(data).apply { parent = this }
    }
}

private fun <T> Iterator<T>.asEnumeration(): Enumeration<T> = object : Enumeration<T> {

    private val iterator: Iterator<T> = this@asEnumeration

    override fun nextElement(): T = iterator.next()
    override fun hasMoreElements(): Boolean = iterator.hasNext()
}
