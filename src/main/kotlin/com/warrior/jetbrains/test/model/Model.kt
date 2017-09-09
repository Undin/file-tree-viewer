package com.warrior.jetbrains.test.model

import com.google.common.eventbus.Subscribe
import com.warrior.jetbrains.test.event.*

interface Model {
    @Subscribe
    fun onStart(event: StartEvent)
    @Subscribe
    fun onNodeSelected(event: NodeSelectedEvent)
    @Subscribe
    fun onPreNodeExpand(event: PreNodeExpandEvent)
    @Subscribe
    fun onAddNewFtpServer(event: AddNewFtpServerEvent)
    @Subscribe
    fun onSetFileFilter(event: SetFileFilterEvent)
}
