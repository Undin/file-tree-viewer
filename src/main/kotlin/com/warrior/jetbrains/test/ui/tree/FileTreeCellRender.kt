package com.warrior.jetbrains.test.ui.tree

import java.awt.Component
import javax.swing.JLabel
import javax.swing.JTree
import javax.swing.tree.DefaultTreeCellRenderer
import javax.swing.tree.TreeCellRenderer

class FileTreeCellRender : TreeCellRenderer {

    private val defaultTreeCellRender: DefaultTreeCellRenderer = DefaultTreeCellRenderer()

    override fun getTreeCellRendererComponent(tree: JTree, value: Any,
                                              selected: Boolean, expanded: Boolean,
                                              leaf: Boolean, row: Int, hasFocus: Boolean): Component {
        val component = defaultTreeCellRender.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus)
        val label = component as? JLabel ?: return component
        when (value) {
            is FileTreeNode -> label.icon = value.userObject.type.smallIcon
            is LoadingNode -> label.icon = null
        }
        return label
    }
}
