package com.warrior.jetbrains.test.view.tree

import javax.swing.tree.DefaultMutableTreeNode

class LoadingNode : DefaultMutableTreeNode("Loading...") {
    override fun getAllowsChildren(): Boolean = false
}
