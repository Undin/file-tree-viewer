package com.warrior.jetbrains.test.ui.content

import com.warrior.jetbrains.test.model.*
import com.warrior.jetbrains.test.ui.icon.ImageIcon
import java.awt.*
import java.util.concurrent.Future
import javax.swing.*

class FolderContentProvider(private val files: List<FileInfo>) : ContentComponentProvider {

    override fun contentComponent(): Component = FolderContentPanel(files)

    private class FolderContentPanel(files: List<FileInfo>): JPanel(GridLayout(0, COLUMNS, GAP, GAP)),
                                                                         Disposable {

        private val tasks: List<Future<*>>

        init {
            background = Color.WHITE
            tasks = ArrayList()
            for (file in files) {
                val label = JLabel(file.name, file.type.icon, SwingConstants.CENTER)
                label.verticalTextPosition = SwingConstants.BOTTOM
                label.horizontalTextPosition = SwingConstants.CENTER
                label.preferredSize = Dimension(ITEM_WIDTH, ITEM_HEIGHT)
                if (file.type == FileType.IMAGE) {
                    tasks += ContentLoader.loadImage(file, ITEM_ICON_SIZE) { image ->
                        if (image != null) {
                            SwingUtilities.invokeLater { label.icon = ImageIcon(image) }
                        }
                    }
                }
                add(label)
            }
        }

        override fun dispose() {
            tasks.forEach { it.cancel(true) }
        }
    }

    companion object {
        private const val ITEM_ICON_SIZE = 64
        private const val ITEM_WIDTH = 72
        private const val ITEM_HEIGHT = 84
        private const val COLUMNS = 5
        private const val GAP = 4
    }
}
