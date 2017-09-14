package com.warrior.jetbrains.test.ui.content.folder

import com.warrior.jetbrains.test.ui.UISizes
import com.warrior.jetbrains.test.ui.content.ContentData
import com.warrior.jetbrains.test.ui.content.Image
import com.warrior.jetbrains.test.ui.content.Text
import com.warrior.jetbrains.test.ui.icon.ImageIcon
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import javax.swing.*

class ItemView(name: String, defaultIcon: Icon) : JPanel() {

    // This property is nullable because it is used in setForeground/setBackground methods
    // which are called during ItemView initialization before label creation
    private val label: JLabel? = JLabel(name, SwingConstants.CENTER).apply {
        horizontalAlignment = SwingConstants.CENTER
        verticalAlignment = SwingConstants.CENTER
        alignmentX = Component.CENTER_ALIGNMENT
    }

    // We support invariant that one of 'imageLabel' and 'textArea' is not null
    // and other is null at same time
    private var imageLabel: JLabel? = createImagePreviewComponent(defaultIcon)
    private var textArea: JTextArea? = null

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        add(Box.createVerticalGlue())
        add(imageLabel)
        add(label)
        add(Box.createVerticalGlue())
        minimumSize = Dimension(ITEM_WIDTH, ITEM_HEIGHT)
        preferredSize = Dimension(ITEM_WIDTH, ITEM_HEIGHT)
    }

    fun setContentData(data: ContentData) {
        when (data) {
            is Image -> setImagePreview(data.image)
            is Text -> setTextPreview(data.text)
        }
    }

    override fun setForeground(fg: Color?) {
        label?.foreground = fg
        imageLabel?.foreground = fg
        textArea?.foreground = fg
        super.setForeground(fg)
    }

    override fun setBackground(bg: Color?) {
        label?.background = bg
        imageLabel?.background = bg
        textArea?.background = bg
        super.setBackground(bg)
    }

    private fun setImagePreview(image: java.awt.Image) {
        val textArea = textArea
        if (textArea != null) {
            val imageLabel = createImagePreviewComponent(ImageIcon(image))
            replace(textArea, imageLabel)
            this.textArea = null
            this.imageLabel = imageLabel
        } else {
            imageLabel?.icon = ImageIcon(image)
        }
        update()
    }

    private fun setTextPreview(text: String) {
        val imageLabel = imageLabel
        if (imageLabel != null) {
            val textArea = createTextPreviewComponent(text)
            replace(imageLabel, textArea)
            this.imageLabel = null
            this.textArea = textArea
        } else {
            textArea?.text = text
        }
        update()
    }

    private fun replace(first: Component, second: Component) {
        val index = components.indexOf(first)
        check(index >= 0) { "Can't find $first in ItemPanel" }
        remove(first)
        add(second, index)
    }

    private fun update() {
        revalidate()
        repaint()
    }

    private fun createImagePreviewComponent(icon: Icon): JLabel = JLabel(icon, SwingConstants.CENTER).apply {
        horizontalAlignment = SwingConstants.CENTER
        verticalAlignment = SwingConstants.CENTER
        alignmentX = Component.CENTER_ALIGNMENT
        preferredSize = Dimension(UISizes.smallPreviewSize, UISizes.smallPreviewSize)
    }

    private fun createTextPreviewComponent(text: String): JTextArea = JTextArea(text).apply {
        isEditable = false
        font = font.deriveFont(FONT_SIZE)
        preferredSize = Dimension(UISizes.smallPreviewSize, UISizes.smallPreviewSize)
        maximumSize = Dimension(UISizes.smallPreviewSize, UISizes.smallPreviewSize)
    }

    companion object {
        val ITEM_WIDTH = UISizes.smallPreviewSize * 5 / 4
        val ITEM_HEIGHT = UISizes.smallPreviewSize * 3 / 2
        const val FONT_SIZE = 3f
    }
}
