package com.warrior.jetbrains.test

import com.warrior.jetbrains.test.event.*
import com.warrior.jetbrains.test.model.FileInfoLoader
import com.warrior.jetbrains.test.model.Model
import com.warrior.jetbrains.test.model.ModelImpl
import com.warrior.jetbrains.test.model.filter.AnyFileFilter
import com.warrior.jetbrains.test.model.filter.ExtensionFileFilter
import com.warrior.jetbrains.test.view.content.*
import com.warrior.jetbrains.test.view.tree.FileTreeNode
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatcher
import org.mockito.Mockito.*
import org.mockito.internal.matchers.InstanceOf

class IntegrationTest {

    private lateinit var view: FakeView
    private lateinit var model: Model

    @Before
    fun setUp() {
        view = mock()
        model = ModelImpl()

        EventBus.register(view)
        EventBus.register(model)
    }

    @After
    fun tearDown() {
        EventBus.unregister(view)
        EventBus.unregister(model)
    }

    @Test
    fun `add roots on start`() {
        StartEvent.post()

        Thread.sleep(100)
        verify(view, atLeastOnce()).addRoot(any())
    }

    @Test
    fun `set children`() {
        val root = FileInfoLoader.resourceFile("root")
        val node = FileTreeNode(root)
        PreNodeExpandEvent(node).post()
        Thread.sleep(1000)

        verify(view).onStartLoadingChildren(StartLoadingChildrenEvent(node))
        val children = FileInfoLoader.getChildrenSync(root)
        verify(view).setNodeChildren(ChildrenLoadedEvent(node, children))
    }

    @Test
    fun `select folder`() {
        val root = FileInfoLoader.resourceFile("root")
        NodeSelectedEvent(FileTreeNode(root)).post()
        Thread.sleep(1000)

        val children = FileInfoLoader.getChildrenSync(root)
        verify(view).onStartLoadingContent(StartLoadingContentEvent)
        verify(view).displayContent(DisplayContentEvent(FileList(children), AnyFileFilter))
        verify(view, times(2)).updateContentData(any())
    }

    @Test
    fun `select generic file`() {
        val file = FileInfoLoader.resourceFile("root/unknown_file")
        NodeSelectedEvent(FileTreeNode(file)).post()
        Thread.sleep(1000)

        verify(view).displayContent(DisplayContentEvent(SingleFile(file), AnyFileFilter))
    }

    @Test
    fun `select archive file`() {
        val archive = FileInfoLoader.resourceFile("root/archive.zip")
        NodeSelectedEvent(FileTreeNode(archive)).post()
        Thread.sleep(1000)

        val children = FileInfoLoader.getChildrenSync(archive)
        verify(view).displayContent(DisplayContentEvent(FileList(children), AnyFileFilter))
    }

    @Test
    fun `select image file`() {
        val image = FileInfoLoader.resourceFile("root/image.png")
        NodeSelectedEvent(FileTreeNode(image)).post()
        Thread.sleep(1000)

        verify(view).displayContent(DisplayContentEvent(SingleFile(image), AnyFileFilter))
        verify(view).updateContentData(argThat(ContentDataLoadedMatcher(Image::class.java)))
    }

    @Test
    fun `select text file`() {
        val text = FileInfoLoader.resourceFile("root/file.txt")
        NodeSelectedEvent(FileTreeNode(text)).post()
        Thread.sleep(1000)

        verify(view).displayContent(DisplayContentEvent(SingleFile(text), AnyFileFilter))
        verify(view).updateContentData(argThat(ContentDataLoadedMatcher(Text::class.java)))
    }

    @Test
    fun `select nothing`() {
        NodeSelectedEvent(null).post()

        Thread.sleep(100)
        verify(view).displayContent(DisplayContentEvent(Empty, AnyFileFilter))
    }

    @Test
    fun `add ftp server`() {
        val user = "user"
        val password = "password"
        val ftpServer = ftp {
            content {
                dir("/dir")
                file("/file.txt")
            }
            user(user, password)
        }

        AddNewFtpServerEvent("localhost:${ftpServer.serverControlPort}", user, password.toCharArray()).post()
        val ftpRoot = FileInfoLoader.createFtpServerRoot("localhost:${ftpServer.serverControlPort}", user, password.toCharArray())
                ?: error("Failed to connect to ftp server")

        Thread.sleep(100)
        verify(view).addRoot(AddRootEvent(ftpRoot))
        ftpServer.stop()
    }

    @Test
    fun `add filter`() {
        val extension = "png"
        SetFileFilterEvent(extension).post()

        Thread.sleep(100)
        verify(view).applyFileFilter(ApplyFileFilterEvent(ExtensionFileFilter(extension)))
    }

    @Test
    fun `add filter several times`() {
        val extensions = listOf("png", "", "pdf")
        val expectedFilters = listOf(ExtensionFileFilter("png"), AnyFileFilter, ExtensionFileFilter("pdf"))
        for ((ext, filter) in extensions.zip(expectedFilters)) {
            SetFileFilterEvent(ext).post()

            Thread.sleep(100)
            verify(view).applyFileFilter(ApplyFileFilterEvent(filter))
        }
    }

    @Test
    fun `add same filter several times`() {
        val extension = "png"
        SetFileFilterEvent(extension).post()
        SetFileFilterEvent(extension).post()

        Thread.sleep(100)
        verify(view).applyFileFilter(ApplyFileFilterEvent(ExtensionFileFilter(extension)))
    }

    @Test
    fun `update content panel after setting file filter`() {
        val extension = "png"
        val expectedFilter = ExtensionFileFilter(extension)

        SetFileFilterEvent(extension).post()

        Thread.sleep(100)
        verify(view).applyFileFilter(ApplyFileFilterEvent(expectedFilter))

        val root = FileInfoLoader.resourceFile("root")
        NodeSelectedEvent(FileTreeNode(root)).post()
        Thread.sleep(1000)

        val children = FileInfoLoader.getChildrenSync(root)
        verify(view).onStartLoadingContent(StartLoadingContentEvent)
        verify(view).displayContent(DisplayContentEvent(FileList(children), expectedFilter))
    }
}

private class ContentDataLoadedMatcher<T: ContentData>(private val clazz: Class<T>) : ArgumentMatcher<ContentDataLoadedEvent> {
    override fun matches(argument: ContentDataLoadedEvent): Boolean = InstanceOf(clazz).matches(argument.data)
}
