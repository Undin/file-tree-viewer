package com.warrior.jetbrains.test.integration

import com.warrior.jetbrains.test.event.EventBus
import com.warrior.jetbrains.test.model.Model
import com.warrior.jetbrains.test.model.ModelImpl
import org.junit.After
import org.junit.Before

abstract class BaseIntegrationTest {

    protected lateinit var view: FakeView
    protected lateinit var model: Model

    @Before
    fun setUp() {
        view = com.warrior.jetbrains.test.mock()
        model = ModelImpl()

        EventBus.register(view)
        EventBus.register(model)
    }

    @After
    fun tearDown() {
        EventBus.unregister(view)
        EventBus.unregister(model)
    }
}
