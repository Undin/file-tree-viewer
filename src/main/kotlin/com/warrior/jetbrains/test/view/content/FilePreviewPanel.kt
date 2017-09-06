package com.warrior.jetbrains.test.view.content

import com.warrior.jetbrains.test.model.FileInfo
import com.warrior.jetbrains.test.view.icon.ImageIcon
import java.awt.Component
import java.awt.GridLayout
import javax.swing.JLabel
import javax.swing.JTextArea
import javax.swing.SwingConstants

class FilePreviewPanel(file: FileInfo): BasePreviewPanel() {

    private var component: Component

    init {
        layout = GridLayout(1, 1)
        val label = JLabel(file.name, file.type.largeIcon, SwingConstants.CENTER)
        label.verticalTextPosition = SwingConstants.BOTTOM
        label.horizontalTextPosition = SwingConstants.CENTER
        component = label
        add(component)
    }

    override fun updateContentData(data: ContentData) {
        when (data) {
            // Change default icon with image
            is Image -> (component as? JLabel)?.icon = ImageIcon(data.image)
            // Replace current component with text area
            is Text -> {
                remove(component)
                component = JTextArea(data.text)
                add(component)
                revalidate()
                repaint()
            }
        }
    }
}
