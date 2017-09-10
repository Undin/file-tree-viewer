package com.warrior.jetbrains.test.ui.content

import com.warrior.jetbrains.test.model.filter.FileFilter
import java.awt.Color
import javax.swing.JPanel

abstract class BasePreviewPanel: JPanel() {

    init {
        background = Color.WHITE
    }

    abstract fun updateContentData(data: ContentData)
    abstract fun applyFileFilter(filter: FileFilter)

    protected fun update() {
        revalidate()
        repaint()
    }
}
