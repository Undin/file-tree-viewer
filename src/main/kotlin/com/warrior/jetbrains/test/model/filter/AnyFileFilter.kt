package com.warrior.jetbrains.test.model.filter

import com.warrior.jetbrains.test.model.FileInfo

object AnyFileFilter : FileFilter {
    override fun accept(file: FileInfo): Boolean = true
    override fun toString(): String = "AnyFilter"
}
