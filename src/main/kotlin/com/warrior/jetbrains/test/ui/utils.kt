package com.warrior.jetbrains.test.ui

import javax.swing.SwingUtilities

fun uiAction(action: () -> Unit) = SwingUtilities.invokeLater(action)
