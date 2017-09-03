package com.warrior.jetbrains.test.ui.content

import java.awt.Component
import javax.swing.JTextArea

class TextPreview(private val text: String) : ContentComponentProvider {
    override fun contentComponent(): Component = JTextArea(text)
}
