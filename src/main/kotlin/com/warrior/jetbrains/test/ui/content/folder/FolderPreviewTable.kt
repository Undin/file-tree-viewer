package com.warrior.jetbrains.test.ui.content.folder

import java.awt.Color
import javax.swing.JTable

class FolderPreviewTable(dataModel: FolderPreviewDataModel) : JTable(dataModel) {

    init {
        background = Color.WHITE
    }

    override fun changeSelection(rowIndex: Int, columnIndex: Int, toggle: Boolean, extend: Boolean) {
        val flatIndex = rowIndex * dataModel.columnCount + columnIndex
        val itemsCount = (dataModel as FolderPreviewDataModel).itemsCount
        if (flatIndex >= itemsCount) return
        super.changeSelection(rowIndex, columnIndex, toggle, extend)
    }
}
