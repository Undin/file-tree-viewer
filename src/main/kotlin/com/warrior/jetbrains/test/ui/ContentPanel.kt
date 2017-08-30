package com.warrior.jetbrains.test.ui

import com.warrior.jetbrains.test.isDirectory
import org.apache.commons.vfs2.FileObject
import java.awt.Color
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

class ContentPanel : JPanel(GridLayout(0, 5, 4, 4)) {

    init {
        background = Color.WHITE
    }

    fun setContent(content: List<FileObject>) {
        removeAll()
        for (file in content) {
            val icon = if (file.isDirectory) Icons.FOLDER_ICON else Icons.FILE_ICON
            val label = JLabel(file.name.baseName, icon, SwingConstants.CENTER)
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
