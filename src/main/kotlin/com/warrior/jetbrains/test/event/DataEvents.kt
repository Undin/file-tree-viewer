package com.warrior.jetbrains.test.event

import com.warrior.jetbrains.test.model.FileInfo
import com.warrior.jetbrains.test.model.filter.FileFilter
import com.warrior.jetbrains.test.view.content.Content
import com.warrior.jetbrains.test.view.content.ContentData
import com.warrior.jetbrains.test.view.tree.FileTreeNode

interface DataEvent : Event

data class AddRootEvent(val root: FileInfo): DataEvent

data class StartLoadingChildrenEvent(val node: FileTreeNode): DataEvent

data class ChildrenLoadedEvent(val node: FileTreeNode, val children: List<FileInfo>): DataEvent

object StartLoadingContentEvent : DataEvent

data class DisplayContentEvent(val content: Content, val filter: FileFilter): DataEvent

data class ContentDataLoadedEvent(val data: ContentData) : DataEvent

data class ApplyFileFilterEvent(val filter: FileFilter) : DataEvent
