package com.warrior.jetbrains.test.view.content

import com.warrior.jetbrains.test.model.ContentLoader
import com.warrior.jetbrains.test.model.FileInfo
import java.util.concurrent.Future
import javax.swing.JTextArea
import javax.swing.SwingUtilities

class TextPreviewPanel(file: FileInfo) : BasePreviewPanel() {

    private var task: Future<*>

    init {
        val label = defaultIcon(file)
        add(label)
        task = ContentLoader.loadText(file) { text ->
            if (text != null) {
                SwingUtilities.invokeLater {
                    remove(label)
                    add(JTextArea(text))
                    revalidate()
                    repaint()
                }
            }
        }
    }

    override fun dispose() {
        task.cancel(true)
    }
}
