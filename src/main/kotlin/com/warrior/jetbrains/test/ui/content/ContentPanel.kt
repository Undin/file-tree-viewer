package com.warrior.jetbrains.test.ui.content

import com.warrior.jetbrains.test.ui.LoadingState
import java.awt.Color
import java.awt.Component
import java.awt.GridLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

class ContentPanel : JPanel(GridLayout(1, 1)) {

    private var state: LoadingState = LoadingState.EMPTY

    private var disposable: Disposable? = null

    init {
        background = Color.WHITE
    }

    fun setContentLoading() {
        if (state != LoadingState.LOADING) {
            // TODO: make more pretty loading view
            setComponent(JLabel("Loading...", SwingConstants.CENTER))
            state = LoadingState.LOADING
        }
    }

    fun setContent(provider: ContentComponentProvider) {
        setComponent(provider.contentComponent())
        state = LoadingState.LOADED
    }

    private fun setComponent(newComponent: Component) {
        disposable?.dispose()
        disposable = newComponent as? Disposable
        removeAll()
        add(newComponent)
        refreshUI()
    }

    private fun refreshUI() {
        revalidate()
        repaint()
    }
}
