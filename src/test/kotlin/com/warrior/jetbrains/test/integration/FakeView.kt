package com.warrior.jetbrains.test.integration

import com.google.common.eventbus.Subscribe
import com.warrior.jetbrains.test.event.*

interface FakeView {

    @Subscribe
    fun addRoot(event: AddRootEvent)

    @Subscribe
    fun onStartLoadingChildren(event: StartLoadingChildrenEvent)
    @Subscribe
    fun setNodeChildren(event: ChildrenLoadedEvent)

    @Subscribe
    fun onStartLoadingContent(event: StartLoadingContentEvent)
    @Subscribe
    fun displayContent(event: DisplayContentEvent)
    @Subscribe
    fun updateContentData(event: ContentDataLoadedEvent)

    @Subscribe
    fun applyFileFilter(event: ApplyFileFilterEvent)

    @Subscribe
    fun onFtpServerResolved(event: FtpServerResolvedEvent)

    @Subscribe
    fun selectedFileInTree(event: SelectFileInTree)
}
