package com.warrior.jetbrains.test.view.content

import com.warrior.jetbrains.test.model.FileInfo
import com.warrior.jetbrains.test.presenter.filter.FileFilter
import com.warrior.jetbrains.test.view.icon.ImageIcon
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.JLabel
import javax.swing.SwingConstants

class FolderPreviewPanel(private val files: List<FileInfo>): BasePreviewPanel() {

    private val labels: MutableMap<FileInfo, JLabel> = HashMap(files.size)

    init {
        layout = GridLayout(0, COLUMNS, GAP, GAP)
        for (file in files) {
            val label = JLabel(file.name, file.type.icon, SwingConstants.CENTER)
            label.verticalTextPosition = SwingConstants.BOTTOM
            label.horizontalTextPosition = SwingConstants.CENTER
            label.preferredSize = Dimension(ITEM_WIDTH, ITEM_HEIGHT)
            add(label)
            labels[file] = label
        }
    }

    override fun updateContentData(data: ContentData) {
        when (data) {
            is Image -> labels[data.file]?.icon = ImageIcon(data.image)
            // TODO: come up with small text preview
            is Text -> { }
        }
    }

    override fun applyFileFilter(filter: FileFilter) {
        removeAll()
        for (file in files) {
            if (filter.accept(file)) {
                // We are sure that there is corresponding label in labels map
                add(labels[file])
            }
        }
        update()
    }

    companion object {
        private const val ITEM_WIDTH = 72
        private const val ITEM_HEIGHT = 84
        private const val COLUMNS = 5
        private const val GAP = 4
    }
}
