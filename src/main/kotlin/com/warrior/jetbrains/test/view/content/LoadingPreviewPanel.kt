package com.warrior.jetbrains.test.view.content

import javax.swing.JLabel
import javax.swing.SwingConstants

class LoadingPreviewPanel : BasePreviewPanel() {

    init {
        add(JLabel("Loading...", SwingConstants.CENTER))
    }

    override fun updateContentData(data: ContentData) {}
}
