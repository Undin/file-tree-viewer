package com.warrior.jetbrains.test.ui.content

import com.google.common.eventbus.Subscribe
import com.warrior.jetbrains.test.event.ApplyFileFilterEvent
import com.warrior.jetbrains.test.event.ContentDataLoadedEvent
import java.awt.GridLayout
import javax.swing.JLabel
import javax.swing.SwingConstants

class LoadingPreviewPanel : BasePreviewPanel() {

    init {
        layout = GridLayout(1, 1)
        add(JLabel("Loading...", SwingConstants.CENTER))
    }

    @Subscribe
    override fun updateContentData(event: ContentDataLoadedEvent) {}
    @Subscribe
    override fun applyFileFilter(event: ApplyFileFilterEvent) {}
}
