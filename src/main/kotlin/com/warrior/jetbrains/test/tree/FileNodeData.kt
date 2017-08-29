package com.warrior.jetbrains.test.tree

import java.nio.file.Path

data class FileNodeData(val path: Path) {
    override fun toString(): String = path.fileName.toString()
}
