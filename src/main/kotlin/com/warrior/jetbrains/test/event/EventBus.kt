package com.warrior.jetbrains.test.event

import com.google.common.eventbus.AsyncEventBus
import com.google.common.eventbus.EventBus
import java.util.concurrent.Executors

/**
 * Global event bus wrapper under guava [AsyncEventBus].
 *
 * Important: async event bus uses single thread executor
 * so all callbacks will be called from same thread.
 */
object EventBus {

    private val eventBus: EventBus = AsyncEventBus(Executors.newSingleThreadExecutor())

    fun register(listener: Any) = eventBus.register(listener)
    fun unregister(listener: Any) = eventBus.unregister(listener)
    fun post(event: Any) = eventBus.post(event)
}
