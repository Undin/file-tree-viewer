package com.warrior.jetbrains.test.ui.tree

import javax.swing.tree.DefaultMutableTreeNode

data class Changes(
        val removedIndexes: IntArray,
        val removedNodes: Array<DefaultMutableTreeNode>,
        val insertedIndexes: IntArray
) {
    companion object {
        fun empty(): Changes = Changes(intArrayOf(), emptyArray(), intArrayOf())
    }
}
