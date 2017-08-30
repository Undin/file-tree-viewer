package com.warrior.jetbrains.test

import java.nio.file.FileSystems
import java.nio.file.Path

val Path.isZip: Boolean get() {
    if (fileSystem != FileSystems.getDefault()) return false
    val stringPath = toString()
    return stringPath.endsWith(".zip") || stringPath.endsWith(".jar")
}

val Path.name: String get() = fileName?.toString()?.removeSuffix("/") ?: toString()
