package com.warrior.jetbrains.test.ui.content

import com.warrior.jetbrains.test.model.FileInfo
import java.awt.Color
import java.awt.Component
import java.awt.GridLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

open class FilePreview(protected val file: FileInfo) : ContentComponentProvider {
    override fun contentComponent(): Component = FilePreviewPanel(file)

    open protected class FilePreviewPanel(protected val file: FileInfo): JPanel(GridLayout(1, 1)), Disposable {

        init {
            background = Color.WHITE
            add(previewComponent(file))
        }

        open protected fun previewComponent(file: FileInfo): Component {
            val label = JLabel(file.name, file.type.largeIcon, SwingConstants.CENTER)
            label.verticalTextPosition = SwingConstants.BOTTOM
            label.horizontalTextPosition = SwingConstants.CENTER
            return label
        }

        override fun dispose() {}
    }
}
