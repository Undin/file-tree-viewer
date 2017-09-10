package com.warrior.jetbrains.test.ui.content

import com.warrior.jetbrains.test.model.filter.FileFilter
import java.awt.GridLayout

class EmptyPreviewPanel(state: Int) : BasePreviewPanel(state) {

    init {
        layout = GridLayout(1, 1)
    }

    override fun updateContentData(data: ContentData) {}
    override fun applyFileFilter(filter: FileFilter) {}
}
