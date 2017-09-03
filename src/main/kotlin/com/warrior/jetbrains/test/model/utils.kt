package com.warrior.jetbrains.test.model

import org.apache.commons.vfs2.FileObject
import java.io.IOException

// TODO: check it for FTP
val FileObject.isDirectory get(): Boolean = try {
    isFolder
} catch (e: IOException) {
    e.printStackTrace()
    false
}
