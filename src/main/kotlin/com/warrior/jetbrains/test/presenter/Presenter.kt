package com.warrior.jetbrains.test.presenter

import com.warrior.jetbrains.test.ui.tree.FileTreeNode

interface Presenter {
    fun onStart()
    fun onNodeSelected(node: FileTreeNode)
    fun onPreNodeExpand(node: FileTreeNode)
    fun onPreNodeCollapse(node: FileTreeNode)
    fun onAddNewFtpServer(host: String, username: String?, password: CharArray)
}
