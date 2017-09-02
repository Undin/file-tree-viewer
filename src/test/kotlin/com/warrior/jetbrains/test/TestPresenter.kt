package com.warrior.jetbrains.test

import com.warrior.jetbrains.test.presenter.PresenterImpl
import com.warrior.jetbrains.test.view.View

class TestPresenter(view: View) : PresenterImpl(view) {
    fun model() = model
}
