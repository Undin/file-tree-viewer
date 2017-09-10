package com.warrior.jetbrains.test.integration

import com.warrior.jetbrains.test.event.*
import com.warrior.jetbrains.test.getChildrenSync
import com.warrior.jetbrains.test.model.FileInfoLoader
import com.warrior.jetbrains.test.model.filter.AnyFileFilter
import com.warrior.jetbrains.test.model.filter.ExtensionFileFilter
import com.warrior.jetbrains.test.resourceFile
import com.warrior.jetbrains.test.ui.content.FileList
import com.warrior.jetbrains.test.ui.tree.FileTreeNode
import org.junit.Test
import org.mockito.Mockito.verify

class FilterTest : BaseIntegrationTest() {

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
