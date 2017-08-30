package com.warrior.jetbrains.test

import org.apache.commons.vfs2.FileObject
import java.io.IOException

val FileObject.isZip: Boolean get() {
    val extension = name.extension
    return extension == "zip" || extension == "jar"
}

val FileObject.isDirectory get(): Boolean = try {
    isFolder
} catch (e: IOException) {
    false
}

val FileObject.childrenSafe get(): Array<FileObject> = try {
    children
} catch (e: IOException) {
    emptyArray()
}
