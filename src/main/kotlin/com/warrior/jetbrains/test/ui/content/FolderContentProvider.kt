package com.warrior.jetbrains.test.ui.content

import com.warrior.jetbrains.test.model.FileInfo
import java.awt.*
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

class FolderContentProvider(private val files: List<FileInfo>) : ContentComponentProvider {

    override fun contentComponent(): Component {
        val panel = JPanel(GridLayout(0, COLUMNS, GAP, GAP))
        panel.background = Color.WHITE
        for (file in files) {
            val icon = file.type.icon
            val label = JLabel(file.name, icon, SwingConstants.CENTER)
            label.verticalTextPosition = SwingConstants.BOTTOM
            label.horizontalTextPosition = SwingConstants.CENTER
            label.preferredSize = Dimension(ITEM_WIDTH, ITEM_HEIGHT)
            panel.add(label)
        }
        return panel
    }

    companion object {
        private const val ITEM_WIDTH = 72
        private const val ITEM_HEIGHT = 80
        private const val COLUMNS = 5
        private const val GAP = 5
    }
}
