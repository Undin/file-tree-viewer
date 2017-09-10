package com.warrior.jetbrains.test.event

/**
 * Interface of all events which program components communicate with each other through event bus.
 */
interface Event {

    /**
     * Send event through event bus
     */
    fun post() {
        EventBus.post(this)
    }
}
