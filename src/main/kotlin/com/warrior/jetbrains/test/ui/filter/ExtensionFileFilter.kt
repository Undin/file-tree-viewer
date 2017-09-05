package com.warrior.jetbrains.test.ui.filter

import com.warrior.jetbrains.test.model.FileInfo
import com.warrior.jetbrains.test.model.extension
import com.warrior.jetbrains.test.model.isFolder

class ExtensionFileFilter(private val extension: String): FileFilter {

    override fun accept(file: FileInfo): Boolean {
        if (file.isFolder) return true
        return extension.equals(file.extension, true)
    }

    override fun toString(): String = "ExtensionFileFilter(extension=$extension)"
}
