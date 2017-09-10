package com.warrior.jetbrains.test.event

import com.warrior.jetbrains.test.view.tree.FileTreeNode

interface UiEvent : Event

object StartEvent : UiEvent {
    override fun toString(): String = "StartEvent"
}

data class NodeSelectedEvent(val node: FileTreeNode?) : UiEvent

data class PreNodeExpandEvent(val node: FileTreeNode) : UiEvent

data class AddNewFtpServerEvent(
        val host: String,
        val username: String?,
        val password: CharArray?,
        val name: String?
) : UiEvent

object CancelResolvingFtpServerEvent : UiEvent {
    override fun toString(): String = "CancelResolvingFtpServerEvent"
}

data class SetFileFilterEvent(val filterString: String) : UiEvent
