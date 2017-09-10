package com.warrior.jetbrains.test.integration

import com.warrior.jetbrains.test.argThat
import com.warrior.jetbrains.test.event.*
import com.warrior.jetbrains.test.getChildrenSync
import com.warrior.jetbrains.test.model.FileInfoLoader
import com.warrior.jetbrains.test.model.filter.AnyFileFilter
import com.warrior.jetbrains.test.resourceFile
import com.warrior.jetbrains.test.view.content.*
import com.warrior.jetbrains.test.view.tree.FileTreeNode
import org.junit.Test
import org.mockito.ArgumentMatcher
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.internal.matchers.InstanceOf

class SelectTest : BaseIntegrationTest() {
    @Test
    fun `select folder`() {
        val root = FileInfoLoader.resourceFile("root")
        NodeSelectedEvent(FileTreeNode(root)).post()
        Thread.sleep(1000)

        val children = FileInfoLoader.getChildrenSync(root)
        verify(view).onStartLoadingContent(StartLoadingContentEvent)
        verify(view).displayContent(DisplayContentEvent(FileList(children), AnyFileFilter))
        verify(view, times(2)).updateContentData(com.warrior.jetbrains.test.any())
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
        Thread.sleep(5000)

        verify(view).displayContent(DisplayContentEvent(SingleFile(image), AnyFileFilter))
        verify(view).updateContentData(argThat(ContentDataLoadedMatcher(Image::class.java)))
    }

    @Test
    fun `select text file`() {
        val text = FileInfoLoader.resourceFile("root/file.txt")
        NodeSelectedEvent(FileTreeNode(text)).post()
        Thread.sleep(5000)

        verify(view).displayContent(DisplayContentEvent(SingleFile(text), AnyFileFilter))
        verify(view).updateContentData(argThat(ContentDataLoadedMatcher(Text::class.java)))
    }

    @Test
    fun `select nothing`() {
        NodeSelectedEvent(null).post()

        Thread.sleep(100)
        verify(view).displayContent(DisplayContentEvent(Empty, AnyFileFilter))
    }
}

private class ContentDataLoadedMatcher<T: ContentData>(private val clazz: Class<T>) : ArgumentMatcher<ContentDataLoadedEvent> {
    override fun matches(argument: ContentDataLoadedEvent): Boolean = InstanceOf(clazz).matches(argument.data)
}
