package com.warrior.jetbrains.test.event

interface Event {
    fun post() {
        println(this)
        EventBus.post(this)
    }
}
