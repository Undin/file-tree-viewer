package com.warrior.jetbrains.test.ui.content

import com.warrior.jetbrains.test.ui.LoadingState
import java.awt.Color
import java.awt.GridLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

class ContentPanel : JPanel(GridLayout(1, 1)) {

    private var state: LoadingState = LoadingState.EMPTY

    init {
        background = Color.WHITE
    }

    fun setContentLoading() {
        if (state != LoadingState.LOADING) {
            removeAll()
            // TODO: make more pretty loading view
            add(JLabel("Loading...", SwingConstants.CENTER))
            refreshUI()
            state = LoadingState.LOADING
        }
    }

    fun setContent(provider: ContentComponentProvider) {
        removeAll()
        add(provider.contentComponent())
        refreshUI()

        state = LoadingState.LOADED
    }

    private fun refreshUI() {
        revalidate()
        repaint()
    }
}
