package com.warrior.jetbrains.test.view

import com.warrior.jetbrains.test.model.FileInfo
import com.warrior.jetbrains.test.view.content.Content
import com.warrior.jetbrains.test.view.tree.FileTreeNode

interface View {
    fun addRoot(root: FileInfo)
    fun onStartLoadingChildren(node: FileTreeNode)
    fun onChildrenLoaded(node: FileTreeNode, children: List<FileTreeNode>)
    fun onStartLoadingContent()
    fun onContentLoaded(content: Content)
}
