package com.warrior.jetbrains.test

import org.apache.commons.vfs2.FileObject
import java.io.IOException

val FileObject.isArchive: Boolean get() {
    val extension = name.extension
    return extension == "zip" || extension == "jar"
}

// TODO: check it for FTP
val FileObject.isDirectory get(): Boolean = try {
    isFolder
} catch (e: IOException) {
    e.printStackTrace()
    false
}
