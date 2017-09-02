package com.warrior.jetbrains.test.model

import com.warrior.jetbrains.test.getChildrenSync
import com.warrior.jetbrains.test.localFile
import org.junit.Test

class LocalModelTest : BaseModelTest() {

    @Test
    fun `get local directory children`() {
        val folder = model.localFile("root")
        checkChildren(folder, "dir", "file.txt")
    }

    @Test
    fun `get zip children`() {
        val zip = model.localFile("archive.zip")
        checkChildren(zip, "zipDir", "zipFile.txt")
    }

    @Test
    fun `get inner zip children`() {
        val zip = model.localFile("outerArchive.zip")
        val innerZip = model.getChildrenSync(zip).find { it.name == "innerArchive.zip" }
                ?: error("'outerArchive.zip' must contain 'innerArchive.zip'")
        checkChildren(innerZip, "zipDir", "zipFile.txt")
    }
}
