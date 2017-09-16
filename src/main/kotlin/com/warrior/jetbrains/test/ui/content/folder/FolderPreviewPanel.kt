package com.warrior.jetbrains.test.ui.content.folder

import com.warrior.jetbrains.test.event.OpenSelectedFileEvent
import com.warrior.jetbrains.test.model.FileInfo
import com.warrior.jetbrains.test.model.filter.FileFilter
import com.warrior.jetbrains.test.ui.content.BasePreviewPanel
import com.warrior.jetbrains.test.ui.content.ContentData
import com.warrior.jetbrains.test.ui.uiAction
import java.awt.GridLayout
import java.awt.event.*
import javax.swing.*

class FolderPreviewPanel(state: Int, files: List<FileInfo>, currentFilter: FileFilter): BasePreviewPanel(state) {

    private val dataModel: FolderPreviewDataModel = FolderPreviewDataModel(files, state, currentFilter)
    private val table = FolderPreviewTable(dataModel)

    init {
        layout = GridLayout(1, 1)
        table.cellSelectionEnabled = true
        table.selectionModel.selectionMode = ListSelectionModel.SINGLE_SELECTION
        table.columnModel.selectionModel.selectionMode = ListSelectionModel.SINGLE_SELECTION
        table.setShowGrid(false)
        table.setDefaultRenderer(FileInfo::class.java, FolderPreviewCellRenderer())
        table.rowHeight = ItemView.ITEM_HEIGHT
        setupTableListeners(table)
        add(table)
    }

    private fun setupTableListeners(table: FolderPreviewTable) {
        table.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (e.clickCount == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    openSelectedItem()
                }
            }
        })
        // We don't use KeyListener because JTable has default action on Enter in action map
        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), OPEN_ITEM_ACTION)
        table.actionMap.put(OPEN_ITEM_ACTION, OpenItemAction(this::openSelectedItem))
    }

    override fun updateContentData(data: ContentData) = uiAction {
        dataModel.updateContentData(data)
    }

    override fun applyFileFilter(filter: FileFilter) = uiAction {
        dataModel.applyFilter(filter)
    }

    private fun openSelectedItem() {
        val row = table.selectedRow
        val column = table.selectedColumn
        if (row < 0 || column < 0) return // nothing selected
        val holder = dataModel.getValueAt(row, column) ?: return
        OpenSelectedFileEvent(holder.file).post()
    }

    companion object {
        private const val OPEN_ITEM_ACTION: String = "OpenItemAction"
    }

    private class OpenItemAction(private val action: () -> Unit) : AbstractAction() {
        override fun actionPerformed(e: ActionEvent) = action()
    }
}
