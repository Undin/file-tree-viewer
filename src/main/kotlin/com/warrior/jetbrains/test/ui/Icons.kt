package com.warrior.jetbrains.test.ui

import javax.swing.Icon
import javax.swing.ImageIcon

object Icons {

    val FOLDER_ICON = icon("icons/folder64.png")
    val FILE_ICON = icon("icons/file64.png")

    private fun icon(path: String): Icon = ImageIcon(javaClass.classLoader.getResource(path))
}
