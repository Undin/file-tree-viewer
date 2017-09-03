package com.warrior.jetbrains.test.model

import org.apache.commons.vfs2.FileObject
import java.io.IOException

val FileObject.isArchive: Boolean get() {
    val extension = name.extension
    return extension == "zip" || extension == "jar"
}

val FileObject.isImage: Boolean get() {
    val extension = name.extension.toLowerCase()
    return extension == "jpg" || extension == "png"
}

// TODO: check it for FTP
val FileObject.isDirectory get(): Boolean = try {
    isFolder
} catch (e: IOException) {
    e.printStackTrace()
    false
}
