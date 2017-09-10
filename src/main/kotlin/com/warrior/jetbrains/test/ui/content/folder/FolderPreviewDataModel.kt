package com.warrior.jetbrains.test.ui.content.folder

import com.google.common.cache.*
import com.warrior.jetbrains.test.event.LoadContentDataEvent
import com.warrior.jetbrains.test.model.*
import com.warrior.jetbrains.test.model.filter.FileFilter
import com.warrior.jetbrains.test.ui.UISizes
import com.warrior.jetbrains.test.ui.content.ContentData
import javax.swing.table.AbstractTableModel

class FolderPreviewDataModel(private val files: List<FileInfo>, private val state: Int,
                             currentFilter: FileFilter) : AbstractTableModel() {

    // ItemView cache allows us to create ItemView lazily
    // and at the same time don't create new ItemView on each request
    private val itemsCache: Cache<FileInfo, ViewHolder>
    private val contentCache: Cache<FileInfo, ContentDataState>

    private var filteredFiles: List<FileInfo>

    init {
        // rough upper bound of max possible count of visible items
        val maxSize = columnCount * (UISizes.screenHeight / ItemView.ITEM_HEIGHT + 3)
        itemsCache = CacheBuilder.newBuilder()
                // All operation will be performed at one thread
                .concurrencyLevel(1)
                .maximumSize(maxSize.toLong())
                .build()
        contentCache = CacheBuilder.newBuilder()
                // All operation will be performed at one thread
                .concurrencyLevel(1)
                .maximumSize(2 * maxSize.toLong())
                .build()
        filteredFiles = files.filter(currentFilter::accept)
    }

    override fun getRowCount(): Int = (filteredFiles.size + columnCount - 1) / columnCount
    override fun getColumnCount(): Int = COLUMN_COUNT

    override fun getValueAt(rowIndex: Int, columnIndex: Int): ItemView? {
        val flatIndex = rowIndex * columnCount + columnIndex
        val file = filteredFiles.getOrNull(flatIndex) ?: return null
        val holder = itemsCache.get(file) {
            val item = ItemView(file.name, file.type.icon)
            ViewHolder(item, rowIndex, columnIndex)
        }
        holder.updatePosition(rowIndex, columnIndex)
        setContent(holder.item, file)
        return holder.item
    }

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean = false
    override fun getColumnClass(columnIndex: Int): Class<*> = FileInfo::class.java

    fun applyFilter(filter: FileFilter) {
        filteredFiles = files.filter(filter::accept)
        fireTableDataChanged()
    }

    fun updateContentData(data: ContentData) {
        contentCache.put(data.file, Loaded(data))
        val holder = itemsCache.getIfPresent(data.file) ?: return
        fireTableCellUpdated(holder.row, holder.column)
    }

    private fun setContent(item: ItemView, file: FileInfo) {
        if (file.needLoadPreview) {
            val content = contentCache.getIfPresent(file)
            when (content) {
                is Loaded -> item.setContentData(content.data)
                is Loading -> {} // wait for update
                null -> {
                    // if cache doesn't contain content ask load it
                    contentCache.put(file, Loading)
                    LoadContentDataEvent(state, file, UISizes.smallPreviewSize).post()
                }
            }
        }
    }

    companion object {
        private const val COLUMN_COUNT = 5
    }
}

private class ViewHolder(val item: ItemView, var row: Int, var column: Int) {
    fun updatePosition(row: Int, column: Int) {
        this.row = row
        this.column = column
    }
}

sealed class ContentDataState
object Loading : ContentDataState()
data class Loaded(val data: ContentData) : ContentDataState()
