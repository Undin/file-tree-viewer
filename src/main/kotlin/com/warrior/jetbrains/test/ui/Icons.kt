package com.warrior.jetbrains.test.ui

import javax.swing.Icon
import javax.swing.ImageIcon

object Icons {

    val FOLDER_ICON = icon("icons/folder64.png")

    val GENERIC_FILE_ICON = icon("icons/file64.png")
    val LARGE_GENERIC_FILE_ICON = icon("icons/file256.png")

    val TEXT_FILE_ICON = icon("icons/text64.png")
    val LARGE_TEXT_FILE_ICON = icon("icons/text256.png")

    val IMAGE_FILE_ICON = GENERIC_FILE_ICON
    val LARGE_IMAGE_FILE_ICON = LARGE_GENERIC_FILE_ICON

    val AUDIO_FILE_ICON = icon("icons/audio64.png")
    val LARGE_AUDIO_FILE_ICON = icon("icons/audio256.png")

    val VIDEO_FILE_ICON = icon("icons/video64.png")
    val LARGE_VIDEO_FILE_ICON = icon("icons/video256.png")

    val ARCHIVE_FILE_ICON = icon("icons/archive64.png")
    val LARGE_ARCHIVE_FILE_ICON = icon("icons/archive256.png")

    private fun icon(path: String): Icon = ImageIcon(javaClass.classLoader.getResource(path))
}
