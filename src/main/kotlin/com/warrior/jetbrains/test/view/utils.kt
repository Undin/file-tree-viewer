package com.warrior.jetbrains.test.view

import javax.swing.SwingUtilities

fun uiAction(action: () -> Unit) = SwingUtilities.invokeLater(action)
