package com.warrior.jetbrains.test.view.content

import com.warrior.jetbrains.test.model.ContentLoader
import com.warrior.jetbrains.test.model.FileInfo
import com.warrior.jetbrains.test.model.FileType
import com.warrior.jetbrains.test.view.icon.ImageIcon
import java.awt.Color
import java.awt.Dimension
import java.awt.GridLayout
import java.util.concurrent.Future
import javax.swing.*

class FolderContentPanel(files: List<FileInfo>): JPanel(GridLayout(0, COLUMNS, GAP, GAP)), Disposable {

    private val tasks: List<Future<*>>

    init {
        background = Color.WHITE
        tasks = ArrayList()
        for (file in files) {
            val label = JLabel(file.name, file.type.icon, SwingConstants.CENTER)
            label.verticalTextPosition = SwingConstants.BOTTOM
            label.horizontalTextPosition = SwingConstants.CENTER
            label.preferredSize = Dimension(ITEM_WIDTH, ITEM_HEIGHT)
            add(label)
            if (file.type == FileType.IMAGE) {
                tasks += ContentLoader.loadImage(file, ITEM_ICON_SIZE) { image ->
                    if (image != null) {
                        SwingUtilities.invokeLater { label.icon = ImageIcon(image) }
                    }
                }
            }

        }
    }

    override fun dispose() {
        tasks.forEach { it.cancel(true) }
    }

    companion object {
        private const val ITEM_ICON_SIZE = 64
        private const val ITEM_WIDTH = 72
        private const val ITEM_HEIGHT = 84
        private const val COLUMNS = 5
        private const val GAP = 4
    }
}
