package com.warrior.jetbrains.test.model

import org.apache.commons.vfs2.FileObject

data class NodeData(val file: FileObject, val name: String) {
    override fun toString(): String = name
}
