package com.warrior.jetbrains.test.ui.content

import com.warrior.jetbrains.test.model.ContentLoader
import com.warrior.jetbrains.test.model.FileInfo
import com.warrior.jetbrains.test.ui.IMAGE_PREVIEW_SIZE
import com.warrior.jetbrains.test.ui.icon.ImageIcon
import java.awt.Component
import java.util.concurrent.Future
import javax.swing.JLabel
import javax.swing.SwingConstants

class ImagePreview(file: FileInfo) : FilePreview(file) {

    override fun contentComponent(): Component = ImagePreviewPanel(file)

    private class ImagePreviewPanel(file: FileInfo) : FilePreviewPanel(file) {

        private var task: Future<*>? = null

        override fun previewComponent(file: FileInfo): Component {
            val label = JLabel(file.name, file.type.largeIcon, SwingConstants.CENTER)
            label.verticalTextPosition = SwingConstants.BOTTOM
            label.horizontalTextPosition = SwingConstants.CENTER
            task = ContentLoader.loadImage(file, IMAGE_PREVIEW_SIZE) { image ->
                if (image != null) {
                    label.icon = ImageIcon(image)
                }
            }
            return label
        }

        override fun dispose() {
            task?.cancel(true)
        }
    }
}
