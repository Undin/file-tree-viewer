package com.warrior.jetbrains.test.model.filter

import com.warrior.jetbrains.test.model.FileInfo

interface FileFilter {
    fun accept(file: FileInfo): Boolean
}
