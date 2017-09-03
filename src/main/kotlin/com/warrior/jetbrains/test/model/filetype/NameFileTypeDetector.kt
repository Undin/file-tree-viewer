package com.warrior.jetbrains.test.model.filetype

import com.warrior.jetbrains.test.model.FileType

// TODO: rewrite this temporary version
class NameFileTypeDetector : FileTypeDetector {
    override fun fileType(fileName: String): FileType {
        val ext = fileName.substringAfterLast(".", "").toLowerCase()
        return when (ext) {
            "txt", "xml", "html", "csv" -> FileType.TEXT
            "jpg", "png", "gif" -> FileType.IMAGE
            "mp3", "flac" -> FileType.AUDIO
            "avi", "mkv" -> FileType.VIDEO
            "zip", "jar" -> FileType.ARCHIVE
            else -> FileType.GENERIC
        }
    }
}
