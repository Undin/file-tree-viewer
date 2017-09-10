package com.warrior.jetbrains.test.ui.icon

import java.awt.Component
import java.awt.Graphics
import java.awt.Image
import javax.swing.Icon

class ImageIcon(private val image: Image) : Icon {
    override fun getIconHeight(): Int = image.getHeight(null)
    override fun getIconWidth(): Int = image.getWidth(null)
    override fun paintIcon(c: Component, g: Graphics, x: Int, y: Int) {
        g.drawImage(image, x, y, c)
    }
}
