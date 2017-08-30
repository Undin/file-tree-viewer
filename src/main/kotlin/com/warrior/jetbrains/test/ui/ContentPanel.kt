package com.warrior.jetbrains.test.ui

import com.warrior.jetbrains.test.name
import java.awt.*
import java.nio.file.Files
import java.nio.file.Path
import javax.swing.*

class ContentPanel : JPanel(GridLayout(0, 5, 4, 4)) {

    init {
        background = Color.WHITE
    }

    fun setContent(content: List<Path>) {
        removeAll()
        for (path in content) {
            val icon = if (Files.isDirectory(path)) Icons.FOLDER_ICON else Icons.FILE_ICON
            val label = JLabel(path.name, icon, SwingConstants.CENTER)
            label.verticalTextPosition = SwingConstants.BOTTOM
            label.horizontalTextPosition = SwingConstants.CENTER
            label.preferredSize = Dimension(ITEM_WIDTH, ITEM_HEIGHT)
            add(label)
        }
        revalidate()
        repaint()
    }

    companion object {
        private const val ITEM_WIDTH = 72
        private const val ITEM_HEIGHT = 80
    }
}
