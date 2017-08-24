package com.warrior.jetbrains.test

import java.nio.file.Path

data class FileNodeData(val path: Path) {
    override fun toString(): String = path.fileName.toString()
}
