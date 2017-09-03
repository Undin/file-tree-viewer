package com.warrior.jetbrains.test.presenter

import com.warrior.jetbrains.test.model.Model
import com.warrior.jetbrains.test.model.canHaveChildren
import com.warrior.jetbrains.test.ui.LoadingState
import com.warrior.jetbrains.test.ui.content.FilePreview
import com.warrior.jetbrains.test.ui.content.FolderContentProvider
import com.warrior.jetbrains.test.ui.tree.FileTreeNode
import com.warrior.jetbrains.test.view.View
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.*
import java.util.concurrent.Future

open class PresenterImpl(private val view: View): Presenter {

    private val logger: Logger = LogManager.getLogger(javaClass)

    protected val model: Model = Model()

    private var contentFuture: Future<*>? = null

    override fun onStart() {
        logger.debug("onStart")
        val roots = model.getLocalRoots()
        for (root in roots) {
            view.addRoot(root)
        }
    }

    override fun onNodeSelected(node: FileTreeNode) {
        logger.debug("onNodeSelected: $node")
        view.onStartLoadingContent()
        contentFuture?.cancel(true)
        contentFuture = model.getChildrenAsync(node.userObject) {
            view.onContentLoaded(it)
        }
    }

    override fun onPreNodeExpand(node: FileTreeNode) {
        logger.debug("onPreNodeExpand: $node")
        if (node.state == LoadingState.EMPTY) {
            view.onStartLoadingChildren(node)
            model.getChildrenAsync(node.userObject) {
                val children = it.map { FileTreeNode(it) }
                view.onChildrenLoaded(node, children)
            }
        }
    }

    override fun onPreNodeCollapse(node: FileTreeNode) {
        logger.debug("onPreNodeCollapse: $node")
    }

    override fun onAddNewFtpServer(host: String, username: String?, password: CharArray) {
        logger.debug("onAddNewFtpServer. host: $host, username: $username, password: ${Arrays.toString(password)}")
        val ftpRoot = model.createFtpServerRoot(host, username, password) ?: return
        view.addRoot(ftpRoot)
    }
}
