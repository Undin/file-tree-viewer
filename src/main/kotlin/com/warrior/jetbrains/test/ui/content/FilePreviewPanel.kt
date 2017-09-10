package com.warrior.jetbrains.test.ui.content

import com.warrior.jetbrains.test.event.LoadContentDataEvent
import com.warrior.jetbrains.test.model.FileInfo
import com.warrior.jetbrains.test.model.filter.FileFilter
import com.warrior.jetbrains.test.model.needLoadPreview
import com.warrior.jetbrains.test.ui.UISizes
import com.warrior.jetbrains.test.ui.icon.ImageIcon
import com.warrior.jetbrains.test.ui.uiAction
import java.awt.Component
import java.awt.GridLayout
import javax.swing.JLabel
import javax.swing.JTextArea
import javax.swing.SwingConstants

class FilePreviewPanel(state: Int, private val file: FileInfo): BasePreviewPanel(state) {

    private var component: Component

    init {
        layout = GridLayout(1, 1)
        val label = JLabel(file.name, file.type.largeIcon, SwingConstants.CENTER)
        label.verticalTextPosition = SwingConstants.BOTTOM
        label.horizontalTextPosition = SwingConstants.CENTER
        component = label
        add(component)
        if (file.needLoadPreview) {
            LoadContentDataEvent(state, file, UISizes.previewSize).post()
        }
    }

    override fun updateContentData(data: ContentData) {
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

    override fun applyFileFilter(filter: FileFilter) = uiAction {
        val accept = filter.accept(file)
        if (accept && component.parent == null) { // file preview should be shown but its component isn't added
            add(component)
        } else if (!accept && component.parent != null) { // file preview shouldn't be shown but its component is added
            remove(component)
        }
        update()
    }
}
