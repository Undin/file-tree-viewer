package com.warrior.jetbrains.test.presenter

import com.warrior.jetbrains.test.model.*
import com.warrior.jetbrains.test.view.*
import com.warrior.jetbrains.test.view.content.*
import com.warrior.jetbrains.test.view.tree.FileTreeNode
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.Future

open class PresenterImpl(private val view: View): Presenter {

    private val logger: Logger = LogManager.getLogger(javaClass)

    protected val model: Model = Model()

    @Volatile
    private var selectedNode: FileTreeNode? = null

    private var contentFuture: Future<*>? = null
    private val contentLoadingTasks: ConcurrentMap<FileInfo, Future<*>> = ConcurrentHashMap()

    override fun onStart() {
        logger.debug("onStart")
        val roots = model.getLocalRoots()
        for (root in roots) {
            view.addRoot(root)
        }
    }

    override fun onNodeSelected(node: FileTreeNode) {
        logger.debug("onNodeSelected: $node")
        selectedNode = node

        println("try cancel content loading: ${contentFuture?.cancel(true)}")
        contentFuture = null
        contentLoadingTasks.forEach {file, task -> println("try cancel content loading of $file: ${task.cancel(true)}") }
        contentLoadingTasks.clear()

        val fileInfo = node.userObject
        if (fileInfo.canHaveChildren) {
            view.onStartLoadingContent()
            contentFuture = model.getChildrenAsync(fileInfo) { files ->
                onContentLoaded(node, FileList(files))
            }
        } else {
            onContentLoaded(node, SingleFile(fileInfo))
        }
    }

    private fun onContentLoaded(node: FileTreeNode, content: Content) {
        view.onContentLoaded(content)
        val (files, imageSize) = when (content) {
            is SingleFile -> listOf(content.file) to IMAGE_PREVIEW_SIZE
            is FileList -> content.files to SMALL_IMAGE_PREVIEW
        }

        loop@for (file in files) {
            // All child files have same location.
            // We can load content of file only if its location is `FileLocation.LOCAL`.
            // So if location of file isn't FileLocation.LOCAL we can skip other children.
            if (file.location != FileLocation.LOCAL) break
            val task = when (file.type) {
                FileType.IMAGE -> ContentLoader.loadImage(file, imageSize) { image ->
                    if (image != null && selectedNode == node) {
                        view.onContentDataLoaded(Image(file, image))
                    }
                }
                FileType.TEXT -> ContentLoader.loadText(file) { text ->
                    if (text != null && selectedNode == node) {
                        view.onContentDataLoaded(Text(file, text))
                    }
                }
                else -> continue@loop
            }
            contentLoadingTasks[file] = task
        }
    }

    override fun onPreNodeExpand(node: FileTreeNode) {
        logger.debug("onPreNodeExpand: $node")
        if (node.state == LoadingState.EMPTY) {
            view.onStartLoadingChildren(node)
            model.getChildrenAsync(node.userObject) { files ->
                view.onChildrenLoaded(node, files)
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
