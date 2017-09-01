package com.warrior.jetbrains.test.ui.tree

import com.warrior.jetbrains.test.isDirectory
import com.warrior.jetbrains.test.isZip
import com.warrior.jetbrains.test.model.FileInfo
import com.warrior.jetbrains.test.ui.LoadingState
import javax.swing.tree.DefaultMutableTreeNode

class FileTreeNode(data: FileInfo) : DefaultMutableTreeNode(data) {

    var state: LoadingState = LoadingState.EMPTY

    override fun getUserObject(): FileInfo = super.getUserObject() as FileInfo
    override fun setUserObject(userObject: Any?) {
        if (userObject !is FileInfo?) error("userObject must be `NodeData`")
        super.setUserObject(userObject)
    }

    override fun getAllowsChildren(): Boolean {
        val file = getUserObject().file
        return file.isDirectory || file.isZip
    }
}
