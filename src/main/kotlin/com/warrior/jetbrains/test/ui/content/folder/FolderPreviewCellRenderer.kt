package com.warrior.jetbrains.test.ui.content.folder

import java.awt.Component
import java.awt.Dimension
import javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer

class FolderPreviewCellRenderer : DefaultTableCellRenderer() {

    override fun getTableCellRendererComponent(table: JTable,
                                               value: Any?,
                                               isSelected: Boolean,
                                               hasFocus: Boolean,
                                               row: Int,
                                               column: Int): Component {
        // Return invisible component if table doesn't have value for this cell
        // to prevent NPE with some LnF UI (for example, SynthLookAndFeel)
        val item = (value as? ViewHolder)?.item ?: return EmptyCell()
        item.background = if (isSelected) table.selectionBackground else table.background
        item.foreground = if (isSelected) table.selectionForeground else table.foreground
        return item
    }

}

private class EmptyCell : Component() {
    init {
        maximumSize = Dimension(0, 0)
        isVisible = false
    }
}
