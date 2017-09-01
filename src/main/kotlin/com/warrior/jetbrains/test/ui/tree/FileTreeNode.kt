package com.warrior.jetbrains.test.ui.tree

import com.warrior.jetbrains.test.isDirectory
import com.warrior.jetbrains.test.isZip
import com.warrior.jetbrains.test.model.NodeData
import com.warrior.jetbrains.test.ui.LoadingState
import javax.swing.tree.DefaultMutableTreeNode

class FileTreeNode(data: NodeData) : DefaultMutableTreeNode(data) {

    var state: LoadingState = LoadingState.EMPTY

    override fun getUserObject(): NodeData = super.getUserObject() as NodeData
    override fun setUserObject(userObject: Any?) {
        if (userObject !is NodeData?) error("userObject must be `NodeData`")
        super.setUserObject(userObject)
    }

    override fun getAllowsChildren(): Boolean {
        val file = getUserObject().file
        return file.isDirectory || file.isZip
    }
}
