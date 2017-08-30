package com.warrior.jetbrains.test.tree

import org.apache.commons.vfs2.FileObject

data class FileNodeData(val file: FileObject) {
    override fun toString(): String = file.name.baseName
}
