package com.warrior.jetbrains.test.event

import com.warrior.jetbrains.test.view.tree.FileTreeNode

interface UiEvent : Event

object StartEvent : UiEvent

data class NodeSelectedEvent(val node: FileTreeNode?) : UiEvent

data class PreNodeExpandEvent(val node: FileTreeNode) : UiEvent

data class AddNewFtpServerEvent(
        val host: String,
        val username: String?,
        val password: CharArray?
) : UiEvent

data class SetFileFilterEvent(val filterString: String) : UiEvent
