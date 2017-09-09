package com.warrior.jetbrains.test.presenter

import com.google.common.eventbus.Subscribe
import com.warrior.jetbrains.test.event.*

interface Presenter {
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
