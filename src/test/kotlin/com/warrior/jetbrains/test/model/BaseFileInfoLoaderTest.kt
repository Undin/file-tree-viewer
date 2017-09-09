package com.warrior.jetbrains.test.model

import com.warrior.jetbrains.test.getChildrenSync
import org.assertj.core.api.Assertions

abstract class BaseFileInfoLoaderTest {

    protected fun checkChildren(file: FileInfo, vararg expectedFiles: TestFile) {
        val children = FileInfoLoader.getChildrenSync(file)
        checkFiles(children, *expectedFiles)
    }

    protected fun checkFiles(files: List<FileInfo>, vararg expectedFiles: TestFile) {
        val testFiles = files.map { (_, name, location, type) -> file(name, location, type) }
        Assertions.assertThat(testFiles).containsExactlyElementsOf(expectedFiles.toList())
    }
    
    protected fun file(name: String, location: FileLocation, type: FileType): TestFile =
            TestFile(name, location, type)

    protected data class TestFile(val name: String, val location: FileLocation, val type: FileType)
}
