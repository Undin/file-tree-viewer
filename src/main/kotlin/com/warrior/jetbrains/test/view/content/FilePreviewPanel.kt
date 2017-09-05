package com.warrior.jetbrains.test.view.content

import com.warrior.jetbrains.test.model.FileInfo

class FilePreviewPanel(file: FileInfo): BasePreviewPanel() {

    init {
        add(defaultIcon(file))
    }
}
