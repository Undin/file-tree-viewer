package com.warrior.jetbrains.test.presenter

import com.warrior.jetbrains.test.model.*
import com.warrior.jetbrains.test.view.LoadingState
import com.warrior.jetbrains.test.view.content.*
import com.warrior.jetbrains.test.view.tree.FileTreeNode
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
        contentFuture?.cancel(true)
        contentFuture = null
        val fileInfo = node.userObject
        if (fileInfo.canHaveChildren) {
            view.onStartLoadingContent()
            contentFuture = model.getChildrenAsync(fileInfo) {
                view.onContentLoaded(FileList(it))
            }
        } else {
            view.onContentLoaded(SingleFile(fileInfo))
        }
    }
//
//    private fun localFilePreview(fileInfo: FileInfo) {
//        when (fileInfo.type) {
//            FileType.IMAGE -> view.onContentLoaded(ImagePreview(fileInfo))
//            FileType.TEXT -> {
//                ContentLoader.loadText(fileInfo) { text ->
//                    val provider = if (text != null) TextPreview(text) else FilePreview(fileInfo)
//                    view.onContentLoaded(provider)
//                }
//
//            }
//            else -> view.onContentLoaded(FilePreview(fileInfo))
//        }
//    }

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
