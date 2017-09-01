package com.warrior.jetbrains.test.view

import com.warrior.jetbrains.test.model.FileInfo
import com.warrior.jetbrains.test.ui.tree.FileTreeNode

interface View {
    fun addRoot(root: FileInfo)
    fun setLoadingState(node: FileTreeNode)
    fun setChildren(node: FileTreeNode, children: List<FileTreeNode>)
    fun onStartLoadingContent()
    fun onContentLoaded(data: List<FileInfo>)
}
