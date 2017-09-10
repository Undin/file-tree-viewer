package com.warrior.jetbrains.test.event

interface Event {
    fun post() {
        EventBus.post(this)
    }
}
