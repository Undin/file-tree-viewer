package com.warrior.jetbrains.test.model

import com.warrior.jetbrains.test.ui.icon.Icons
import org.apache.commons.vfs2.FileObject
import javax.swing.Icon

data class FileInfo(
        val file: FileObject,
        val name: String,
        val location: FileLocation,
        val type: FileType
) {
    override fun toString(): String = name
}

val FileInfo.path: String get() = file.name.path

val FileInfo.isLocal: Boolean get() = location == FileLocation.LOCAL || location == FileLocation.ARCHIVE
val FileInfo.isFolder: Boolean get() = type == FileType.FOLDER
val FileInfo.isArchive: Boolean get() = type == FileType.ARCHIVE
val FileInfo.canHaveChildren: Boolean get() = isFolder || isLocal && isArchive

enum class FileType(val smallIcon: Icon, val icon: Icon, val largeIcon: Icon) {
    TEXT(Icons.SMALL_TEXT_FILE_ICON, Icons.TEXT_FILE_ICON, Icons.LARGE_TEXT_FILE_ICON),
    IMAGE(Icons.SMALL_IMAGE_FILE_ICON, Icons.IMAGE_FILE_ICON, Icons.LARGE_IMAGE_FILE_ICON),
    AUDIO(Icons.SMALL_AUDIO_FILE_ICON, Icons.AUDIO_FILE_ICON, Icons.LARGE_AUDIO_FILE_ICON),
    VIDEO(Icons.SMALL_VIDEO_FILE_ICON, Icons.VIDEO_FILE_ICON, Icons.LARGE_VIDEO_FILE_ICON),
    ARCHIVE(Icons.SMALL_ARCHIVE_FILE_ICON, Icons.ARCHIVE_FILE_ICON, Icons.LARGE_ARCHIVE_FILE_ICON),
    FOLDER(Icons.SMALL_FOLDER_ICON, Icons.FOLDER_ICON, Icons.FOLDER_ICON),
    GENERIC(Icons.SMALL_GENERIC_FILE_ICON, Icons.GENERIC_FILE_ICON, Icons.LARGE_GENERIC_FILE_ICON)
}

enum class FileLocation {
    LOCAL,
    ARCHIVE,
    FTP
}
