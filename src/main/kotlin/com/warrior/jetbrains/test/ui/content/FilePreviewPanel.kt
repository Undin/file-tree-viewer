package com.warrior.jetbrains.test.ui.content

import com.warrior.jetbrains.test.model.FileInfo

class FilePreviewPanel(file: FileInfo): BasePreviewPanel() {

    init {
        add(defaultIcon(file))
    }
}
