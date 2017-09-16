package com.warrior.jetbrains.test.model

import com.google.common.eventbus.Subscribe
import com.warrior.jetbrains.test.event.*
import com.warrior.jetbrains.test.model.filter.AnyFileFilter
import com.warrior.jetbrains.test.model.filter.ExtensionFileFilter
import com.warrior.jetbrains.test.model.filter.FileFilter
import com.warrior.jetbrains.test.ui.LoadingState
import com.warrior.jetbrains.test.ui.content.*
import com.warrior.jetbrains.test.ui.tree.FileTreeNode
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.Future

class ModelImpl : Model {

    private val logger: Logger = LogManager.getLogger(javaClass)

    private var currentFilterString: String = ""
    @Volatile
    private var currentFilter: FileFilter = AnyFileFilter

    // Contains all tasks of root FileInfo to quickly cancel all tasks after removing root node
    // rootFile -> (file -> future)
    private val childrenLoadingTasks: ConcurrentMap<FileInfo, ConcurrentMap<FileInfo, Future<*>>> = ConcurrentHashMap()

    private var contentFuture: Future<*>? = null
    private val contentLoadingTasks: MutableList<Future<*>> = ArrayList()

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

        contentFuture?.cancel(true)
        contentFuture = null
        contentLoadingTasks.forEach { it.cancel(true) }
        contentLoadingTasks.clear()

        if (node != null) {
            val fileInfo = node.userObject
            if (fileInfo.canHaveChildren) {
                StartLoadingContentEvent.post()
                contentFuture = FileInfoLoader.getChildrenAsync(fileInfo) { files ->
                    if (node.state != LoadingState.LOADED) {
                        ChildrenLoadedEvent(node, files).post()
                    }
                    DisplayContentEvent(FileList(files), currentFilter).post()
                }
            } else {
                DisplayContentEvent(SingleFile(fileInfo), currentFilter).post()
            }
        } else {
            DisplayContentEvent(Empty, currentFilter).post()
        }
    }

    @Subscribe
    override fun onLoadContentData(event: LoadContentDataEvent) {
        logger.debug("onLoadContentData: $event")
        val (state, file, imageSize) = event
        val task = when (file.type) {
            FileType.IMAGE -> ContentLoader.loadImage(file, imageSize) { image ->
                if (image != null) {
                    ContentDataLoadedEvent(state, Image(file, image)).post()
                }
            }
            FileType.TEXT -> ContentLoader.loadText(file) { text ->
                if (text != null) {
                    ContentDataLoadedEvent(state, Text(file, text)).post()
                }
            }
            else -> return
        }
        contentLoadingTasks.add(task)
    }

    @Subscribe
    override fun onPreNodeExpand(event: PreNodeExpandEvent) {
        logger.debug("onPreNodeExpand: $event")
        val node = event.node
        if (node.state == LoadingState.EMPTY) {
            StartLoadingChildrenEvent(node).post()
            val file = node.userObject
            val rootFile = node.rootUserObject
            val rootTasks = childrenLoadingTasks.getOrPut(rootFile) { ConcurrentHashMap() }
            val task = FileInfoLoader.getChildrenAsync(file) { files ->
                ChildrenLoadedEvent(node, files).post()
                rootTasks.remove(file)
            }
            rootTasks[file] = task
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

    @Subscribe
    override fun onRootRemoved(event: RootRemovedEvent) {
        logger.debug("onRootRemoved: $event")
        for ((_, task) in childrenLoadingTasks[event.root].orEmpty()) {
            task.cancel(true)
        }
    }

    @Subscribe
    override fun onOpenSelectedFile(event: OpenSelectedFileEvent) {
        logger.debug("onOpenSelectedFile: $event")
        // Just select file in tree.
        // It'll call children loading of corresponding tree node and preview updating .
        SelectFileInTree(event.file).post()
    }
}

private val FileTreeNode.rootUserObject: FileInfo get() {
    var node = this
    while (!node.userObject.isRoot) {
        node = node.parent as FileTreeNode // It cast is safe because only parent of root is not FileTreeNode
    }
    return node.userObject
}
