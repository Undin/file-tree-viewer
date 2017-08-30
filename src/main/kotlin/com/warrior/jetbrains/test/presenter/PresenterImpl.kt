package com.warrior.jetbrains.test.presenter

import com.warrior.jetbrains.test.model.Model
import com.warrior.jetbrains.test.tree.FileTreeNode
import com.warrior.jetbrains.test.view.View

class PresenterImpl(private val view: View): Presenter {

    private val model: Model = Model(this)

    override fun onNodeSelected(node: FileTreeNode) {
        view.setContentData(model.getChildren(node.userObject.file))
    }

    override fun onPreNodeExpand(node: FileTreeNode) {
        node.updateChildren(model.getChildren(node.userObject.file))
    }

    override fun onPreNodeCollapse(node: FileTreeNode) {
    }
}
