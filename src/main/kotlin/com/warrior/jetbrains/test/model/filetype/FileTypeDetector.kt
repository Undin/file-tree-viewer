package com.warrior.jetbrains.test.model.filetype

import com.warrior.jetbrains.test.model.FileType

interface FileTypeDetector {
    fun fileType(fileName: String): FileType
}
