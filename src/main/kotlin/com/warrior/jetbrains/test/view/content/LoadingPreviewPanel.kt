package com.warrior.jetbrains.test.view.content

import com.warrior.jetbrains.test.presenter.filter.FileFilter
import javax.swing.JLabel
import javax.swing.SwingConstants

class LoadingPreviewPanel : BasePreviewPanel() {

    init {
        add(JLabel("Loading...", SwingConstants.CENTER))
    }

    override fun updateContentData(data: ContentData) {}
    override fun applyFileFilter(filter: FileFilter) {}
}
