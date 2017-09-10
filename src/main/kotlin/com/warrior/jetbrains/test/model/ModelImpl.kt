package com.warrior.jetbrains.test.model

import com.google.common.eventbus.Subscribe
import com.warrior.jetbrains.test.event.*
import com.warrior.jetbrains.test.model.filter.AnyFileFilter
import com.warrior.jetbrains.test.model.filter.ExtensionFileFilter
import com.warrior.jetbrains.test.model.filter.FileFilter
import com.warrior.jetbrains.test.view.LoadingState
import com.warrior.jetbrains.test.view.UISizes
import com.warrior.jetbrains.test.view.content.*
import com.warrior.jetbrains.test.view.tree.FileTreeNode
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.Future

class ModelImpl : Model {

    private val logger: Logger = LogManager.getLogger(javaClass)

    @Volatile
    private var selectedNode: FileTreeNode? = null

    private var currentFilterString: String = ""
    @Volatile
    private var currentFilter: FileFilter = AnyFileFilter

    private var contentFuture: Future<*>? = null
    private val contentLoadingTasks: ConcurrentMap<FileInfo, Future<*>> = ConcurrentHashMap()

    private var ftpFuture: Future<*>? = null

    @Subscribe
    override fun onStart(event: StartEvent) {
        logger.debug("onStart")
        val roots = FileInfoLoader.getLocalRoots()
        for (root in roots) {
            AddRootEvent(root).post()
        }
    }

    @Subscribe
    override fun onNodeSelected(event: NodeSelectedEvent) {
        logger.debug("onNodeSelected: $event")
        val node = event.node
        selectedNode = node

        contentFuture?.cancel(true)
        contentFuture = null
        contentLoadingTasks.forEach { _, task -> task.cancel(true) }
        contentLoadingTasks.clear()

        if (node != null) {
            val fileInfo = node.userObject
            if (fileInfo.canHaveChildren) {
                StartLoadingContentEvent.post()
                contentFuture = FileInfoLoader.getChildrenAsync(fileInfo) { files ->
                    onContentLoaded(node, FileList(files))
                }
            } else {
                onContentLoaded(node, SingleFile(fileInfo))
            }
        } else {
            DisplayContentEvent(Empty, currentFilter).post()
        }
    }

    private fun onContentLoaded(node: FileTreeNode, content: Content) {
        DisplayContentEvent(content, currentFilter).post()
        val (files, imageSize) = when (content) {
            is Empty -> emptyList<FileInfo>() to 0
            is SingleFile -> listOf(content.file) to UISizes.previewSize
            is FileList -> content.files to UISizes.smallPreviewSize
        }

        loop@for (file in files) {
            // All child files have same location.
            // We can load content of file only if its location is `FileLocation.LOCAL`.
            // So if location of file isn't FileLocation.LOCAL we can skip other children.
            if (file.location != FileLocation.LOCAL) break
            val task = when (file.type) {
                FileType.IMAGE -> ContentLoader.loadImage(file, imageSize) { image ->
                    if (image != null && selectedNode == node) {
                        ContentDataLoadedEvent(Image(file, image)).post()
                    }
                }
                FileType.TEXT -> ContentLoader.loadText(file) { text ->
                    if (text != null && selectedNode == node) {
                        ContentDataLoadedEvent(Text(file, text)).post()
                    }
                }
                else -> continue@loop
            }
            contentLoadingTasks[file] = task
        }
    }

    @Subscribe
    override fun onPreNodeExpand(event: PreNodeExpandEvent) {
        logger.debug("onPreNodeExpand: $event")
        val node = event.node
        if (node.state == LoadingState.EMPTY) {
            StartLoadingChildrenEvent(node).post()
            FileInfoLoader.getChildrenAsync(node.userObject) { files ->
                ChildrenLoadedEvent(node, files).post()
            }
        }
    }

    @Subscribe
    override fun onAddNewFtpServer(event: AddNewFtpServerEvent) {
        logger.debug("onAddNewFtpServer: $event")
        ftpFuture?.cancel(true)

        val (host, username, password, name) = event
        ftpFuture = FileInfoLoader.resolveFtpServerAsync(host, username, password, name) { result ->
            FtpServerResolvedEvent(result).post()
            if (result is Ok) {
                AddRootEvent(result.value).post()
            }
        }
    }

    @Subscribe
    override fun onCancelResolvingFtpServer(event: CancelResolvingFtpServerEvent) {
        logger.debug("onCancelResolvingFtpServer: $event")
        ftpFuture?.cancel(true)
        ftpFuture = null
    }

    @Subscribe
    override fun onSetFileFilter(event: SetFileFilterEvent) {
        logger.debug("onSetFileFilter: $event")
        val filterString = event.filterString
        if (filterString != currentFilterString) {
            currentFilterString = filterString
            currentFilter = if (filterString.isEmpty()) AnyFileFilter else ExtensionFileFilter(filterString)
            ApplyFileFilterEvent(currentFilter).post()
        }
    }
}
