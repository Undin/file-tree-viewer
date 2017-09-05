package com.warrior.jetbrains.test.view.icon

import javax.swing.Icon
import javax.swing.ImageIcon

object Icons {

    // TODO: use svg icons
    val SMALL_FOLDER_ICON = icon("icons/folder16.png")
    val FOLDER_ICON = icon("icons/folder64.png")

    val SMALL_GENERIC_FILE_ICON = icon("icons/file16.png")
    val GENERIC_FILE_ICON = icon("icons/file64.png")
    val LARGE_GENERIC_FILE_ICON = icon("icons/file256.png")

    val SMALL_TEXT_FILE_ICON = icon("icons/text16.png")
    val TEXT_FILE_ICON = icon("icons/text64.png")
    val LARGE_TEXT_FILE_ICON = icon("icons/text256.png")

    val SMALL_IMAGE_FILE_ICON = icon("icons/image16.png")
    val IMAGE_FILE_ICON = icon("icons/image64.png")
    val LARGE_IMAGE_FILE_ICON = icon("icons/image256.png")

    val SMALL_AUDIO_FILE_ICON = icon("icons/audio16.png")
    val AUDIO_FILE_ICON = icon("icons/audio64.png")
    val LARGE_AUDIO_FILE_ICON = icon("icons/audio256.png")

    val SMALL_VIDEO_FILE_ICON = icon("icons/video16.png")
    val VIDEO_FILE_ICON = icon("icons/video64.png")
    val LARGE_VIDEO_FILE_ICON = icon("icons/video256.png")

    val SMALL_ARCHIVE_FILE_ICON = icon("icons/archive16.png")
    val ARCHIVE_FILE_ICON = icon("icons/archive64.png")
    val LARGE_ARCHIVE_FILE_ICON = icon("icons/archive256.png")

    private fun icon(path: String): Icon = ImageIcon(javaClass.classLoader.getResource(path))
}
