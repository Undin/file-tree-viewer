package com.warrior.jetbrains.test.ui.content

import com.warrior.jetbrains.test.model.FileInfo
import java.awt.Color
import java.awt.GridLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

abstract class BasePreviewPanel: JPanel(GridLayout(1, 1)), Disposable {

    init {
        background = Color.WHITE
    }

    protected fun defaultIcon(file: FileInfo): JLabel {
        val label = JLabel(file.name, file.type.largeIcon, SwingConstants.CENTER)
        label.verticalTextPosition = SwingConstants.BOTTOM
        label.horizontalTextPosition = SwingConstants.CENTER
        return label
    }

    override fun dispose() {}
}