package com.warrior.jetbrains.test.ui.content

import com.google.common.eventbus.Subscribe
import com.warrior.jetbrains.test.event.ApplyFileFilterEvent
import com.warrior.jetbrains.test.event.ContentDataLoadedEvent
import com.warrior.jetbrains.test.model.FileInfo
import com.warrior.jetbrains.test.ui.icon.ImageIcon
import com.warrior.jetbrains.test.ui.uiAction
import java.awt.Component
import java.awt.GridLayout
import javax.swing.JLabel
import javax.swing.JTextArea
import javax.swing.SwingConstants

class FilePreviewPanel(private val file: FileInfo): BasePreviewPanel() {

    private var component: Component

    init {
        layout = GridLayout(1, 1)
        val label = JLabel(file.name, file.type.largeIcon, SwingConstants.CENTER)
        label.verticalTextPosition = SwingConstants.BOTTOM
        label.horizontalTextPosition = SwingConstants.CENTER
        component = label
        add(component)
    }

    @Subscribe
    override fun updateContentData(event: ContentDataLoadedEvent) = uiAction {
        val data = event.data
        when (data) {
        // Change default icon with image
            is Image -> (component as? JLabel)?.icon = ImageIcon(data.image)
        // Replace current component with text area
            is Text -> {
                remove(component)
                component = JTextArea(data.text).apply { isEditable = false }
                add(component)
                revalidate()
                repaint()
            }
        }
    }

    @Subscribe
    override fun applyFileFilter(event: ApplyFileFilterEvent) = uiAction {
        val accept = event.filter.accept(file)
        if (accept && component.parent == null) { // file preview should be shown but its component isn't added
            add(component)
        } else if (!accept && component.parent != null) { // file preview shouldn't be shown but its component is added
            remove(component)
        }
        update()
    }
}
