package com.warrior.jetbrains.test

import com.warrior.jetbrains.test.model.Model
import com.warrior.jetbrains.test.presenter.Presenter
import com.warrior.jetbrains.test.presenter.filter.AnyFileFilter
import com.warrior.jetbrains.test.presenter.filter.ExtensionFileFilter
import com.warrior.jetbrains.test.view.View
import com.warrior.jetbrains.test.view.content.*
import com.warrior.jetbrains.test.view.tree.FileTreeNode
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class IntegrationTest {

    private lateinit var view: View
    private lateinit var presenter: Presenter
    private lateinit var model: Model

    @Before
    fun setUp() {
        view = mock()
        presenter = TestPresenter(view)
        model = (presenter as TestPresenter).model()
    }

    @Test
    fun `add roots on start`() {
        presenter.onStart()
        verify(view, atLeastOnce()).addRoot(any())
    }

    @Test
    fun `set children`() {
        val root = model.localFile("root")
        val node = FileTreeNode(root)
        presenter.onPreNodeExpand(node)
        Thread.sleep(1000)

        verify(view).onStartLoadingChildren(node)
        val children = model.getChildrenSync(root)
        verify(view).onChildrenLoaded(node, children)
    }

    @Test
    fun `select folder`() {
        val root = model.localFile("root")
        presenter.onNodeSelected(FileTreeNode(root))
        Thread.sleep(1000)

        verify(view).onStartLoadingContent()
        verify(view).displayContent(any(FileList::class.java))
        verify(view, times(2)).onContentDataLoaded(any(ContentData::class.java))
    }

    @Test
    fun `select generic file`() {
        val file = model.localFile("root/unknown_file")
        presenter.onNodeSelected(FileTreeNode(file))
        Thread.sleep(1000)

        verify(view).displayContent(any(SingleFile::class.java))
    }

    @Test
    fun `select archive file`() {
        val archive = model.localFile("root/archive.zip")
        presenter.onNodeSelected(FileTreeNode(archive))
        Thread.sleep(1000)

        verify(view).displayContent(any(FileList::class.java))
    }

    @Test
    fun `select image file`() {
        val image = model.localFile("root/image.png")
        presenter.onNodeSelected(FileTreeNode(image))
        Thread.sleep(1000)

        verify(view).displayContent(any(SingleFile::class.java))
        verify(view).onContentDataLoaded(any(Image::class.java))
    }

    @Test
    fun `select text file`() {
        val image = model.localFile("root/file.txt")
        presenter.onNodeSelected(FileTreeNode(image))
        Thread.sleep(1000)

        verify(view).displayContent(any(SingleFile::class.java))
        verify(view).onContentDataLoaded(any(Text::class.java))
    }

    @Test
    fun `select nothing`() {
        presenter.onNodeSelected(null)

        verify(view).displayContent(Empty)
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

        presenter.onAddNewFtpServer("localhost:${ftpServer.serverControlPort}", user, password.toCharArray())
        val ftpRoot = model.createFtpServerRoot("localhost:${ftpServer.serverControlPort}", user, password.toCharArray())
                ?: error("Failed to connect to ftp server")

        verify(view).addRoot(ftpRoot)
        ftpServer.stop()
    }

    @Test
    fun `add filter`() {
        val extension = "png"
        presenter.onAddFileFilter(extension)

        verify(view).applyFileFilter(ExtensionFileFilter(extension))
    }

    @Test
    fun `add filter several times`() {
        val extensions = listOf("png", "", "pdf")
        val expectedFilters = listOf(ExtensionFileFilter("png"), AnyFileFilter, ExtensionFileFilter("pdf"))
        for ((ext, filter) in extensions.zip(expectedFilters)) {
            presenter.onAddFileFilter(ext)
            verify(view).applyFileFilter(filter)
        }
    }

    @Test
    fun `add same filter several times`() {
        val extension = "png"
        presenter.onAddFileFilter(extension)
        presenter.onAddFileFilter(extension)

        verify(view).applyFileFilter(ExtensionFileFilter(extension))
    }
}
