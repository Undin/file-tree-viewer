package com.warrior.jetbrains.test.tree

import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.MutableTreeNode
import javax.swing.tree.TreeNode
import kotlin.collections.HashMap

// TODO: remove unused children to prevent unnecessary memory using
// TODO: load children asynchronously
class FileTreeNode(data: FileNodeData) : DefaultMutableTreeNode(data) {

    private var childPaths: List<Path> = emptyList()
    private val childNodes: MutableMap<Path, FileTreeNode> = HashMap()

    fun updateChildren(paths: List<Path>) {
        childPaths = paths
        childNodes.clear()
    }

    override fun getUserObject(): FileNodeData = super.getUserObject() as FileNodeData
    override fun setUserObject(userObject: Any?) {
        if (userObject !is FileNodeData?) error("userObject must be `FileNodeData`")
        super.setUserObject(userObject)
    }

    override fun getAllowsChildren(): Boolean = Files.isDirectory(getUserObject().path)

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
        val path = aChild.getUserObject().path
        return childPaths.indexOf(path)
    }

    private fun getChild(path: Path): FileTreeNode = childNodes.getOrPut(path) {
        FileTreeNode(FileNodeData(path)).apply { parent = this }
    }
}

private fun <T> Iterator<T>.asEnumeration(): Enumeration<T> = object : Enumeration<T> {

    private val iterator: Iterator<T> = this@asEnumeration

    override fun nextElement(): T = iterator.next()
    override fun hasMoreElements(): Boolean = iterator.hasNext()
}
