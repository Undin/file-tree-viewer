package com.warrior.jetbrains.test.view.content

import com.warrior.jetbrains.test.model.ContentLoader
import com.warrior.jetbrains.test.model.FileInfo
import com.warrior.jetbrains.test.view.IMAGE_PREVIEW_SIZE
import com.warrior.jetbrains.test.view.icon.ImageIcon
import java.util.concurrent.Future
import javax.swing.SwingUtilities

class ImagePreviewPanel(file: FileInfo) : BasePreviewPanel() {

    private var task: Future<*>

    init {
        val label = defaultIcon(file)
        add(label)
        task = ContentLoader.loadImage(file, IMAGE_PREVIEW_SIZE) { image ->
            if (image != null) {
                SwingUtilities.invokeLater { label.icon = ImageIcon(image) }
            }
        }
    }

    override fun dispose() {
        task.cancel(true)
    }
}
