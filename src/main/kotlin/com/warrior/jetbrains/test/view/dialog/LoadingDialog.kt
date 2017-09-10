package com.warrior.jetbrains.test.view.dialog

import com.warrior.jetbrains.test.event.CancelResolvingFtpServerEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.*

class LoadingDialog(owner: JDialog) : BaseDialog(owner, "Connecting to FTP server") {

    private val optionPane: JOptionPane

    init {
        val progressBar = JProgressBar()
        progressBar.isIndeterminate = true

        optionPane = JOptionPane(progressBar, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION,
                null, arrayOf("Cancel"))
        optionPane.addPropertyChangeListener(this)
        contentPane = optionPane

        pack()
        setLocationRelativeTo(owner)

        defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent) {
                cancelFtpServerResolving()
            }
        })
    }

    override fun getOptionPane(): JOptionPane = optionPane

    override fun onPropertyChange(value: Any) {
        cancelFtpServerResolving()
        dispose()
    }

    private fun cancelFtpServerResolving() {
        CancelResolvingFtpServerEvent.post()
    }
}
