package com.warrior.jetbrains.test.model.filetype

import com.warrior.jetbrains.test.model.FileType

// TODO: rewrite this temporary version
class NameFileTypeDetector : FileTypeDetector {
    override fun fileType(fileName: String): FileType {
        val ext = fileName.substringAfterLast(".", "").toLowerCase()
        return when (ext) {
            "txt", "xml", "html", "csv" -> FileType.TEXT
            "jpg", "jpeg", "png", "gif" -> FileType.IMAGE
            "mp3", "flac", "wav", "aac" -> FileType.AUDIO
            "avi", "mkv", "mov", "mp4", "m4v" -> FileType.VIDEO
            "zip", "jar" -> FileType.ARCHIVE
            else -> FileType.GENERIC
        }
    }
}
