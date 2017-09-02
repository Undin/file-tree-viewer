package com.warrior.jetbrains.test.model

import com.warrior.jetbrains.test.getChildrenSync
import org.assertj.core.api.Assertions

abstract class BaseModelTest {

    protected var model: Model = Model()

    protected fun checkChildren(file: FileInfo, vararg expectedNames: String) {
        val children = model.getChildrenSync(file)
        val names = children.map { it.name }
        Assertions.assertThat(names).containsExactlyElementsOf(expectedNames.toList())
    }
}
