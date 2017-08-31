package com.warrior.jetbrains.test.presenter

import com.warrior.jetbrains.test.model.Model
import com.warrior.jetbrains.test.ui.tree.FileTreeNode
import com.warrior.jetbrains.test.view.View
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class PresenterImpl(private val view: View): Presenter {

    private val logger: Logger = LogManager.getLogger(javaClass)
    private val model: Model = Model(this)

    override fun onStart() {
        logger.debug("onStart")
        val roots = model.getLocalRoots()
        for (root in roots) {
            view.addRoot(root)
        }
    }

    override fun onNodeSelected(node: FileTreeNode) {
        logger.debug("onNodeSelected: $node")
        // TODO: load children asynchronously
        view.setContentData(model.getChildren(node.userObject.file))
    }

    override fun onPreNodeExpand(node: FileTreeNode) {
        logger.debug("onPreNodeExpand: $node")
        // TODO: load children asynchronously
        node.updateChildren(model.getChildren(node.userObject.file))
    }

    override fun onPreNodeCollapse(node: FileTreeNode) {
        logger.debug("onPreNodeCollapse: $node")
    }

    override fun onAddNewFtpServer(host: String, username: String?, password: CharArray) {
        logger.debug("onAddNewFtpServer. host: $host, username: $username, password: $password")
        val ftpRoot = model.createFtpServerRoot(host, username, password) ?: return
        view.addRoot(ftpRoot)
    }
}
