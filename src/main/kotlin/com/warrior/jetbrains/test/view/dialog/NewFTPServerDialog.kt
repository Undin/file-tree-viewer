package com.warrior.jetbrains.test.view.dialog

import com.google.common.eventbus.Subscribe
import com.warrior.jetbrains.test.event.AddNewFtpServerEvent
import com.warrior.jetbrains.test.event.FtpServerResolvedEvent
import com.warrior.jetbrains.test.model.Err
import com.warrior.jetbrains.test.model.Ok
import com.warrior.jetbrains.test.view.UISizes
import com.warrior.jetbrains.test.view.uiAction
import net.miginfocom.layout.AC
import net.miginfocom.layout.CC
import net.miginfocom.layout.LC
import net.miginfocom.swing.MigLayout
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.awt.Dimension
import java.awt.Frame
import javax.swing.*

class NewFTPServerDialog(owner: Frame) : BaseDialog(owner, "Add new FTP server") {

    private val logger: Logger = LogManager.getLogger(javaClass)

    private val host: JTextField = JTextField()
    private val username: JTextField = JTextField()
    private val password: JPasswordField = JPasswordField()
    private val name: JTextField = JTextField()

    private val optionPane: JOptionPane

    private var loadingDialog: LoadingDialog? = null

    init {
        val layout = MigLayout(
                LC().fillX(),
                AC().align("left").gap("rel").grow().fill(),
                AC().gap("10"))
        val panel = JPanel(layout)
                .row("Host:", host)
                .row("Username:", username)
                .row("Password:", password)
                .row("Name:", name)

        optionPane = JOptionPane(panel, JOptionPane.QUESTION_MESSAGE,
                JOptionPane.YES_NO_OPTION, null, arrayOf(CANCEL, OK))
        optionPane.preferredSize = Dimension(UISizes.ftpDialogWidth, UISizes.ftpDialogHeight)
        optionPane.addPropertyChangeListener(this)
        contentPane = optionPane

        isResizable = false
        pack()
        setLocationRelativeTo(owner)
    }

    override fun getOptionPane(): JOptionPane = optionPane

    override fun onPropertyChange(value: Any) {
        when (value) {
            OK -> {
                val dialog = LoadingDialog(this)
                val hostText = host.text
                if (hostText.isNullOrBlank()) {
                    JOptionPane.showMessageDialog(this, "Host can't be empty", "Invalid host", JOptionPane.ERROR_MESSAGE)
                } else {
                    AddNewFtpServerEvent(hostText, username.text, password.password, name.text).post()
                    loadingDialog = dialog
                    dialog.isVisible = true
                }
            }
            CANCEL -> dispose()
        }
    }

    @Subscribe
    fun onResolveFtpServer(event: FtpServerResolvedEvent) = uiAction {
        logger.debug("onResolveFtpServer: $event")
        loadingDialog?.dispose()
        loadingDialog = null

        val result = event.result
        when (result) {
            is Ok -> dispose()
            is Err -> JOptionPane.showMessageDialog(this, result.error, "Error", JOptionPane.ERROR_MESSAGE)
        }
    }

    private fun JPanel.row(label: String, textField: JTextField): JPanel {
        add(JLabel(label))
        add(textField, CC().wrap())
        return this
    }

    companion object {
        private const val OK = "Ok"
        private const val CANCEL = "Cancel"
    }
}
