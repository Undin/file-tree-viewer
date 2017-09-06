package com.warrior.jetbrains.test.view.content

import java.awt.GridLayout

class EmptyPreviewPanel : BasePreviewPanel() {

    init {
        layout = GridLayout(1, 1)
    }

    override fun updateContentData(data: ContentData) {}
}
