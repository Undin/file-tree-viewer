package com.warrior.jetbrains.test

import com.warrior.jetbrains.test.presenter.Presenter
import com.warrior.jetbrains.test.presenter.PresenterImpl
import com.warrior.jetbrains.test.view.FileViewerFrame
import com.warrior.jetbrains.test.view.uiAction
import javax.swing.UIManager

object FileViewer {

    private val presenter: Presenter = PresenterImpl()

    @JvmStatic
    fun main(args: Array<String>) {
        // TODO: find out behaviour on not mac os
        System.setProperty("apple.laf.useScreenMenuBar", "true")
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        uiAction { createUI() }
    }

    private fun createUI() {
        val frame = FileViewerFrame()
        frame.isVisible = true
    }
}
