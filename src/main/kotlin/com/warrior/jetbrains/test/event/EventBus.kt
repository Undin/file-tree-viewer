package com.warrior.jetbrains.test.event

import com.google.common.eventbus.AsyncEventBus
import com.google.common.eventbus.EventBus
import java.util.concurrent.Executors

object EventBus {

    private val eventBus: EventBus = AsyncEventBus(Executors.newSingleThreadExecutor())

    fun register(listener: Any) = eventBus.register(listener)
    fun unregister(listener: Any) = eventBus.unregister(listener)
    fun post(event: Any) = eventBus.post(event)
}
