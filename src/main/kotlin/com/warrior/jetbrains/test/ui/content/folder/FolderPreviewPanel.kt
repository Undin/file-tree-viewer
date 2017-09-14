package com.warrior.jetbrains.test.ui.content.folder

import com.warrior.jetbrains.test.model.FileInfo
import com.warrior.jetbrains.test.model.filter.FileFilter
import com.warrior.jetbrains.test.ui.content.BasePreviewPanel
import com.warrior.jetbrains.test.ui.content.ContentData
import com.warrior.jetbrains.test.ui.uiAction
import java.awt.GridLayout
import javax.swing.ListSelectionModel

class FolderPreviewPanel(state: Int, files: List<FileInfo>, currentFilter: FileFilter): BasePreviewPanel(state) {

    private val dataModel: FolderPreviewDataModel = FolderPreviewDataModel(files, state, currentFilter)

    init {
        layout = GridLayout(1, 1)
        val table = FolderPreviewTable(files.size, dataModel)
        table.cellSelectionEnabled = true
        table.selectionModel.selectionMode = ListSelectionModel.SINGLE_SELECTION
        table.setShowGrid(false)
        table.setDefaultRenderer(FileInfo::class.java, FolderPreviewCellRenderer())
        table.rowHeight = ItemView.ITEM_HEIGHT
        add(table)
    }

    override fun updateContentData(data: ContentData) = uiAction {
        dataModel.updateContentData(data)
    }

    override fun applyFileFilter(filter: FileFilter) = uiAction {
        dataModel.applyFilter(filter)
    }
}
