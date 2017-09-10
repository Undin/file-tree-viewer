package com.warrior.jetbrains.test.ui

import java.awt.Dimension
import java.awt.GraphicsEnvironment
import java.awt.Toolkit

object UISizes {

    private const val DEFAULT_WIDTH = 1280
    private const val DEFAULT_HEIGHT = 800

    private val screenSize = if (!GraphicsEnvironment.isHeadless()) { // Check for CI server
        Toolkit.getDefaultToolkit().screenSize
    } else {
        Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT)
    }
    private val width = Math.max(DEFAULT_WIDTH, screenSize.width)
    private val height = Math.max(DEFAULT_HEIGHT, screenSize.height)

    val initialWidth = height
    val initialHeight = height * 5 / 8

    val initialDividerLocation = initialWidth / 4
    val dividerSize = 2

    val ftpDialogWidth = width * 2 / 5
    val ftpDialogHeight = height / 3

    val smallPreviewSize = height * 8 / 100
    val previewSize = height * 5 / 8
}
