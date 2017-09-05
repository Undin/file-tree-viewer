package com.warrior.jetbrains.test.view.tree

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
        val file = (value as? FileTreeNode)?.userObject ?: return component
        label.icon = file.type.smallIcon
        return label
    }
}
