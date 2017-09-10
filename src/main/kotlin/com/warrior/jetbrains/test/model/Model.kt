package com.warrior.jetbrains.test.model

import com.google.common.eventbus.Subscribe
import com.warrior.jetbrains.test.event.*

interface Model {
    @Subscribe
    fun onStart(event: StartEvent)
    @Subscribe
    fun onNodeSelected(event: NodeSelectedEvent)
    @Subscribe
    fun onLoadContentData(event: LoadContentDataEvent)
    @Subscribe
    fun onPreNodeExpand(event: PreNodeExpandEvent)
    @Subscribe
    fun onAddNewFtpServer(event: AddNewFtpServerEvent)
    @Subscribe
    fun onCancelResolvingFtpServer(event: CancelResolvingFtpServerEvent)
    @Subscribe
    fun onSetFileFilter(event: SetFileFilterEvent)
    @Subscribe
    fun onRootRemoved(event: RootRemovedEvent)
}
