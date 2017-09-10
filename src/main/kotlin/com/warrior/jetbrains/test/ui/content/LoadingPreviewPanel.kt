package com.warrior.jetbrains.test.ui.content

import com.warrior.jetbrains.test.model.filter.FileFilter
import java.awt.GridLayout
import javax.swing.JLabel
import javax.swing.SwingConstants

class LoadingPreviewPanel(state: Int) : BasePreviewPanel(state) {

    init {
        layout = GridLayout(1, 1)
        add(JLabel("Loading...", SwingConstants.CENTER))
    }

    override fun updateContentData(data: ContentData) {}
    override fun applyFileFilter(filter: FileFilter) {}
}
