package com.warrior.jetbrains.test.ui.content.folder

import com.warrior.jetbrains.test.model.FileInfo

class ViewHolder(val file: FileInfo, var row: Int, var column: Int) {

    val item: ItemView = ItemView(file.name, file.type.icon)

    fun updatePosition(row: Int, column: Int) {
        this.row = row
        this.column = column
    }
}
