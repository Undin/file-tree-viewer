package com.warrior.jetbrains.test.ui.dialog

import java.awt.Frame
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import javax.swing.JDialog
import javax.swing.JOptionPane

abstract class BaseDialog : JDialog, PropertyChangeListener {

    constructor(owner: Frame, title: String) : super(owner, title, true)
    constructor(owner: JDialog, title: String) : super(owner, title, true)

    override fun propertyChange(e: PropertyChangeEvent) {
        val prop = e.propertyName
        val optionPane = getOptionPane()

        if (isVisible && e.source === optionPane &&
                (JOptionPane.VALUE_PROPERTY == prop || JOptionPane.INPUT_VALUE_PROPERTY == prop)) {
            val value = optionPane.value
            if (value == JOptionPane.UNINITIALIZED_VALUE) return

            optionPane.value = JOptionPane.UNINITIALIZED_VALUE
            onPropertyChange(value)
        }
    }

    abstract protected fun getOptionPane(): JOptionPane
    abstract protected fun onPropertyChange(value: Any)
}
