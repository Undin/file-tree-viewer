package com.warrior.jetbrains.test.model.filter

import com.warrior.jetbrains.test.getChildrenSync
import com.warrior.jetbrains.test.model.BaseModelTest
import com.warrior.jetbrains.test.model.FileLocation
import com.warrior.jetbrains.test.model.FileType
import com.warrior.jetbrains.test.resourceFile
import org.junit.Test

class FilterTest : BaseModelTest() {

    @Test
    fun `any filter`() = doTest(AnyFileFilter,
            file("archive.zip", FileLocation.LOCAL, FileType.ARCHIVE),
            file("dir", FileLocation.LOCAL, FileType.FOLDER),
            file("file.txt", FileLocation.LOCAL, FileType.TEXT),
            file("image.png", FileLocation.LOCAL, FileType.IMAGE),
            file("outerArchive.zip", FileLocation.LOCAL, FileType.ARCHIVE),
            file("unknown_file", FileLocation.LOCAL, FileType.GENERIC)
    )

    @Test
    fun `extension filter`() = doTest(ExtensionFileFilter("zip"),
            file("archive.zip", FileLocation.LOCAL, FileType.ARCHIVE),
            file("dir", FileLocation.LOCAL, FileType.FOLDER),
            file("outerArchive.zip", FileLocation.LOCAL, FileType.ARCHIVE)
    )

    private fun doTest(filter: FileFilter, vararg expectedFiles: TestFile) {
        val root = model.resourceFile("root")
        val children = model.getChildrenSync(root)
        val filteredChildren = children.filter(filter::accept)
        checkFiles(filteredChildren, *expectedFiles)
    }
}
