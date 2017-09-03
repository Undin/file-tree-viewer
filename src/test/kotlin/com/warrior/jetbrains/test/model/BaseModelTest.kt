package com.warrior.jetbrains.test.model

import com.warrior.jetbrains.test.getChildrenSync
import org.assertj.core.api.Assertions

abstract class BaseModelTest {

    protected var model: Model = Model()

    protected fun checkChildren(file: FileInfo, vararg expectedFiles: TestFile) {
        val children = model.getChildrenSync(file)
        val files = children.map { (_, name, location, type) -> file(name, location, type) }
        Assertions.assertThat(files).containsExactlyElementsOf(expectedFiles.toList())
    }
    
    protected fun file(name: String, location: FileLocation, type: FileType): TestFile =
            TestFile(name, location, type)

    protected data class TestFile(val name: String, val location: FileLocation, val type: FileType)
}
