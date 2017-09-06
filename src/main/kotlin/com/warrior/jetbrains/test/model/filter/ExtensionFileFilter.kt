package com.warrior.jetbrains.test.model.filter

import com.warrior.jetbrains.test.model.FileInfo
import com.warrior.jetbrains.test.model.extension
import com.warrior.jetbrains.test.model.isFolder

class ExtensionFileFilter(private val extension: String): FileFilter {

    override fun accept(file: FileInfo): Boolean {
        if (file.isFolder) return true
        return extension.equals(file.extension, true)
    }

    override fun toString(): String = "ExtensionFileFilter(extension=$extension)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ExtensionFileFilter

        if (extension != other.extension) return false

        return true
    }

    override fun hashCode(): Int = extension.hashCode()
}
