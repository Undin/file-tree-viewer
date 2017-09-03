package com.warrior.jetbrains.test.ui.content

import com.warrior.jetbrains.test.model.FileInfo
import java.awt.Color
import java.awt.Component
import java.awt.GridLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

class FilePreview(private val file: FileInfo) : ContentComponentProvider {
    override fun contentComponent(): Component {
        val panel = JPanel(GridLayout(1, 1))
        panel.background = Color.WHITE
        val label = JLabel(file.name, file.type.largeIcon, SwingConstants.CENTER)
        label.verticalTextPosition = SwingConstants.BOTTOM
        label.horizontalTextPosition = SwingConstants.CENTER
        panel.add(label)
        return panel
    }
}
