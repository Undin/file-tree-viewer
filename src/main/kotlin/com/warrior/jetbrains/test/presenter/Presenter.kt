package com.warrior.jetbrains.test.presenter

import com.warrior.jetbrains.test.tree.FileTreeNode

interface Presenter {
    fun onNodeSelected(node: FileTreeNode)
    fun onPreNodeExpand(node: FileTreeNode)
    fun onPreNodeCollapse(node: FileTreeNode)
}
