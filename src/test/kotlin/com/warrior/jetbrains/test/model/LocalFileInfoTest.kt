package com.warrior.jetbrains.test.model

import com.warrior.jetbrains.test.getChildrenSync
import com.warrior.jetbrains.test.resourceFile
import com.warrior.jetbrains.test.model.FileLocation.*
import com.warrior.jetbrains.test.model.FileType.*
import org.junit.Test

class LocalFileInfoTest : BaseFileInfoLoaderTest() {

    @Test
    fun `get local directory children`() {
        val folder = FileInfoLoader.resourceFile("root", true)
        checkChildren(folder,
                file("archive.zip", LOCAL, FileType.ARCHIVE),
                file("dir", LOCAL, FOLDER),
                file("file.txt", LOCAL, TEXT),
                file("image.png", LOCAL, IMAGE),
                file("outerArchive.zip", LOCAL, FileType.ARCHIVE),
                file("unknown_file", LOCAL, GENERIC)
        )
    }

    @Test
    fun `get zip children`() {
        val zip = FileInfoLoader.resourceFile("root/archive.zip", true)
        checkChildren(zip,
                file("zipDir", FileLocation.ARCHIVE, FOLDER),
                file("zipFile.txt", FileLocation.ARCHIVE, TEXT)
        )
    }

    @Test
    fun `get inner zip children`() {
        val zip = FileInfoLoader.resourceFile("root/outerArchive.zip", true)
        val innerZip = FileInfoLoader.getChildrenSync(zip).find { it.name == "innerArchive.zip" }
                ?: error("'outerArchive.zip' must contain 'innerArchive.zip'")
        checkChildren(innerZip,
                file("zipDir", FileLocation.ARCHIVE, FOLDER),
                file("zipFile.txt", FileLocation.ARCHIVE, TEXT))
    }
}
