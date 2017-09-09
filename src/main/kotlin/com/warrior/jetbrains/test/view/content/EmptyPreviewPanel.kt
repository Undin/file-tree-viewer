package com.warrior.jetbrains.test.view.content

import com.google.common.eventbus.Subscribe
import com.warrior.jetbrains.test.event.ApplyFileFilterEvent
import com.warrior.jetbrains.test.event.ContentDataLoadedEvent
import java.awt.GridLayout

class EmptyPreviewPanel : BasePreviewPanel() {

    init {
        layout = GridLayout(1, 1)
    }

    @Subscribe
    override fun updateContentData(event: ContentDataLoadedEvent) {}
    @Subscribe
    override fun applyFileFilter(event: ApplyFileFilterEvent) {}
}
