package com.warrior.jetbrains.test.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.nio.file.Paths

class LocalModelTest {

    private val model = Model()

    @Test
    fun `get local directory children`() {
        val folder = localFile("root")
        checkChildren(folder, "dir", "file.txt")
    }

    @Test
    fun `get zip children`() {
        val zip = localFile("archive.zip")
        checkChildren(zip, "zipDir", "zipFile.txt")
    }

    @Test
    fun `get inner zip children`() {
        val zip = localFile("outerArchive.zip")
        val innerZip = getChildrenSync(zip).find { it.name == "innerArchive.zip" }
                ?: error("'outerArchive.zip' must contain 'innerArchive.zip'")
        checkChildren(innerZip, "zipDir", "zipFile.txt")
    }

    private fun checkChildren(file: FileInfo, vararg expectedNames: String) {
        val children = getChildrenSync(file)
        val names = children.map { it.name }
        assertThat(names).containsExactlyElementsOf(expectedNames.toList())
    }

    private fun getChildrenSync(file: FileInfo): List<FileInfo> {
        var children: List<FileInfo>? = null
        model.getChildrenAsync(file) { children = it }.get()
        return children ?: error("Result list is supposed to be not empty")
    }

    private fun localFile(path: String): FileInfo {
        val realPath = Paths.get(javaClass.classLoader.getResource(path).path)
        return model.getLocalFile(realPath) ?: error("Failed to create 'FileInfo' from $path")
    }
}
