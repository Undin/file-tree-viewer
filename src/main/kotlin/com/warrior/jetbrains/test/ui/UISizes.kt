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

    val screenWidth = Math.max(DEFAULT_WIDTH, screenSize.width)
    val screenHeight = Math.max(DEFAULT_HEIGHT, screenSize.height)

    val initialWidth = screenHeight
    val initialHeight = screenHeight * 5 / 8

    val initialDividerLocation = initialWidth / 4
    val dividerSize = 2

    val ftpDialogWidth = screenWidth * 2 / 5
    val ftpDialogHeight = screenHeight / 3

    val smallPreviewSize = screenHeight * 8 / 100
    val previewSize = screenHeight * 5 / 8
}
