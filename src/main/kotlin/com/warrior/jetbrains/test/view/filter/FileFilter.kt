package com.warrior.jetbrains.test.view.filter

import com.warrior.jetbrains.test.model.FileInfo

interface FileFilter {
    fun accept(file: FileInfo): Boolean
}
