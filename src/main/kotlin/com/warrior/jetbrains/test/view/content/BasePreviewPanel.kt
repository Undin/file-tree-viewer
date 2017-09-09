package com.warrior.jetbrains.test.view.content

import com.google.common.eventbus.Subscribe
import com.warrior.jetbrains.test.event.ApplyFileFilterEvent
import com.warrior.jetbrains.test.event.ContentDataLoadedEvent
import java.awt.Color
import javax.swing.JPanel

abstract class BasePreviewPanel: JPanel() {

    init {
        background = Color.WHITE
    }

    @Subscribe
    abstract fun updateContentData(event: ContentDataLoadedEvent)
    @Subscribe
    abstract fun applyFileFilter(event: ApplyFileFilterEvent)

    protected fun update() {
        revalidate()
        repaint()
    }
}
