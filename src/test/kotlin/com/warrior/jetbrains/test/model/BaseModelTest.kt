package com.warrior.jetbrains.test.model

import org.assertj.core.api.Assertions

abstract class BaseModelTest {

    protected var model: Model = Model()

    protected fun checkChildren(file: FileInfo, vararg expectedNames: String) {
        val children = getChildrenSync(file)
        val names = children.map { it.name }
        Assertions.assertThat(names).containsExactlyElementsOf(expectedNames.toList())
    }

    protected fun getChildrenSync(file: FileInfo): List<FileInfo> {
        var children: List<FileInfo>? = null
        model.getChildrenAsync(file) { children = it }.get()
        return children ?: error("Result list is supposed to be not empty")
    }
}
