package com.warrior.jetbrains.test.ui.content

import com.google.common.eventbus.Subscribe
import com.warrior.jetbrains.test.event.ApplyFileFilterEvent
import com.warrior.jetbrains.test.event.ContentDataLoadedEvent
import com.warrior.jetbrains.test.model.FileInfo
import com.warrior.jetbrains.test.model.filter.FileFilter
import com.warrior.jetbrains.test.ui.UISizes
import com.warrior.jetbrains.test.ui.icon.ImageIcon
import com.warrior.jetbrains.test.ui.uiAction
import java.awt.*
import javax.swing.*

class FolderPreviewPanel(private val files: List<FileInfo>, currentFilter: FileFilter): BasePreviewPanel() {

    private val items: MutableMap<FileInfo, ItemPanel> = HashMap(files.size)

    init {
        layout = GridLayout(0, COLUMNS, GAP, GAP)
        for (file in files) {
            val itemPanel = ItemPanel(file.name, file.type.icon)
            items[file] = itemPanel
            if (currentFilter.accept(file)) {
                add(itemPanel)
            }
        }
    }

    @Subscribe
    override fun updateContentData(event: ContentDataLoadedEvent) = uiAction {
        val data = event.data
        when (data) {
            is Image -> items[data.file]?.setImagePreview(data.image)
            is Text -> items[data.file]?.setTextPreview(data.text)
        }
    }

    @Subscribe
    override fun applyFileFilter(event: ApplyFileFilterEvent) = uiAction {
        removeAll()
        for (file in files) {
            if (event.filter.accept(file)) {
                // We are sure that there is corresponding component in items map
                add(items[file])
            }
        }
        update()
    }

    companion object {
        private const val COLUMNS = 5
        private const val GAP = 4
    }
}

private class ItemPanel(name: String, defaultIcon: Icon) : JPanel() {

    private var imageLabel: JLabel?
    private var textArea: JTextArea? = null

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        background = Color.WHITE
        add(Box.createVerticalGlue())
        imageLabel = JLabel(defaultIcon, SwingConstants.CENTER).apply {
            horizontalAlignment = SwingConstants.CENTER
            verticalAlignment = SwingConstants.CENTER
            alignmentX = Component.CENTER_ALIGNMENT
            preferredSize = Dimension(UISizes.smallPreviewSize, UISizes.smallPreviewSize)

        }
        add(imageLabel)
        val label = JLabel(name, SwingConstants.CENTER).apply {
            horizontalAlignment = SwingConstants.CENTER
            verticalAlignment = SwingConstants.CENTER
            alignmentX = Component.CENTER_ALIGNMENT
        }
        add(label)
        add(Box.createVerticalGlue())
        minimumSize = Dimension(ITEM_WIDTH, ITEM_HEIGHT)
        preferredSize = Dimension(ITEM_WIDTH, ITEM_HEIGHT)
    }

    fun setImagePreview(image: java.awt.Image) {
        imageLabel?.icon = ImageIcon(image)
    }

    fun setTextPreview(text: String) {
        val imageLabel = imageLabel
        if (imageLabel != null) {
            val textArea = createTextPreviewComponent(text)

            val index = components.indexOf(imageLabel)
            check(index >= 0) { "Can't find image label in ItemPanel" }

            remove(imageLabel)
            add(textArea, index)

            this.imageLabel = null
            this.textArea = textArea
        } else {
            textArea?.text = text
        }
        revalidate()
        repaint()
    }

    private fun createTextPreviewComponent(text: String): JTextArea = JTextArea(text).apply {
        isEditable = false
        font = font.deriveFont(FONT_SIZE)
        preferredSize = Dimension(UISizes.smallPreviewSize, UISizes.smallPreviewSize)
        maximumSize = Dimension(UISizes.smallPreviewSize, UISizes.smallPreviewSize)
    }

    companion object {
        private val ITEM_WIDTH = UISizes.smallPreviewSize * 18 / 16
        private val ITEM_HEIGHT = UISizes.smallPreviewSize * 5 / 4
        private const val FONT_SIZE = 3f
    }
}
