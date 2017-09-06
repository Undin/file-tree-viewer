package com.warrior.jetbrains.test.model

import com.warrior.jetbrains.test.getChildrenSync
import org.assertj.core.api.Assertions
import org.junit.Before

abstract class BaseModelTest {

    protected lateinit var model: Model

    @Before
    fun setUp() {
        model = Model()
    }

    protected fun checkChildren(file: FileInfo, vararg expectedFiles: TestFile) {
        val children = model.getChildrenSync(file)
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
