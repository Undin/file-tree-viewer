package com.warrior.jetbrains.test.ui.content

import com.warrior.jetbrains.test.model.FileInfo
import javax.swing.Icon

class ImagePreview(file: FileInfo, private val preview: Icon) : FilePreview(file) {
    override fun fileIcon(): Icon = preview
}
