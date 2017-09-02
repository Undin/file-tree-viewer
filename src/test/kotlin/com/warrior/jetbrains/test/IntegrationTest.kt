package com.warrior.jetbrains.test

import com.warrior.jetbrains.test.model.Model
import com.warrior.jetbrains.test.presenter.Presenter
import com.warrior.jetbrains.test.ui.tree.FileTreeNode
import com.warrior.jetbrains.test.view.View
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.atLeastOnce
import org.mockito.Mockito.verify

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
        val children = model.getChildrenSync(root).map(::FileTreeNode)
        verify(view).onChildrenLoaded(node, children)
    }

    @Test
    fun `set content`() {
        val root = model.localFile("root")
        presenter.onNodeSelected(FileTreeNode(root))
        Thread.sleep(1000)

        verify(view).onStartLoadingContent()
        verify(view).onContentLoaded(model.getChildrenSync(root))
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
}
