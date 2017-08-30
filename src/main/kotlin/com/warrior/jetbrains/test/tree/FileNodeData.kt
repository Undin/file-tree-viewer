package com.warrior.jetbrains.test.tree

import com.warrior.jetbrains.test.name
import java.nio.file.Path

data class FileNodeData(val path: Path) {
    override fun toString(): String = path.name
}
