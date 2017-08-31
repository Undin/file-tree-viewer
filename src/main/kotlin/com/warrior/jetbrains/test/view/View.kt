package com.warrior.jetbrains.test.view

import com.warrior.jetbrains.test.model.NodeData

interface View {
    fun addRoot(root: NodeData)
    fun setContentData(data: List<NodeData>)
}
