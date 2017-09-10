package com.warrior.jetbrains.test.event

import com.warrior.jetbrains.test.model.FileInfo
import com.warrior.jetbrains.test.ui.tree.FileTreeNode

/**
 * Root interface of events which ui components send to model
 */
interface UiEvent : Event

object StartEvent : UiEvent {
    override fun toString(): String = "StartEvent"
}

data class NodeSelectedEvent(val node: FileTreeNode?) : UiEvent

data class LoadContentDataEvent(val state: Int, val file: FileInfo, val imageSize: Int = 0) : UiEvent

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

data class RootRemovedEvent(val root: FileInfo) : UiEvent
