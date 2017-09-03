package com.warrior.jetbrains.test.ui.tree

import com.warrior.jetbrains.test.model.FileInfo
import com.warrior.jetbrains.test.model.canHaveChildren
import com.warrior.jetbrains.test.ui.LoadingState
import javax.swing.tree.DefaultMutableTreeNode

class FileTreeNode(data: FileInfo) : DefaultMutableTreeNode(data) {

    var state: LoadingState = LoadingState.EMPTY

    override fun getUserObject(): FileInfo = super.getUserObject() as FileInfo
    override fun setUserObject(userObject: Any?) {
        if (userObject !is FileInfo?) error("userObject must be `NodeData`")
        super.setUserObject(userObject)
    }

    override fun getAllowsChildren(): Boolean = getUserObject().canHaveChildren

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FileTreeNode

        if (getUserObject() != other.getUserObject()) return false

        return true
    }

    override fun hashCode(): Int = getUserObject().hashCode()
}
