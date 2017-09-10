package com.warrior.jetbrains.test.ui.content.folder

import com.warrior.jetbrains.test.*
import com.warrior.jetbrains.test.event.EventBus
import com.warrior.jetbrains.test.event.LoadContentDataEvent
import com.warrior.jetbrains.test.model.*
import com.warrior.jetbrains.test.model.filter.AnyFileFilter
import com.warrior.jetbrains.test.ui.UISizes
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

class IntegrationFolderPreviewDataModelTest {

    private val root: FileInfo = FileInfoLoader.resourceFile("root", true)
    private val children: List<FileInfo> = FileInfoLoader.getChildrenSync(root)

    private lateinit var model: Model

    @Before
    fun setUp() {
        model = mock()
        EventBus.register(model)
    }

    @After
    fun tearDown() {
        EventBus.unregister(model)
    }

    @Test
    fun `load content data events`() {
        val state = 0
        val dataModel = FolderPreviewDataModel(children, state, AnyFileFilter)
        for (row in 0 until dataModel.rowCount) {
            for (column in 0 until dataModel.columnCount) {
                dataModel.getValueAt(row, column)
            }
        }
        Thread.sleep(1000)

        verify(model, times(2)).onLoadContentData(any())
    }

    @Test
    fun `don't load content data twice`() {
        val state = 0
        val dataModel = FolderPreviewDataModel(children, state, AnyFileFilter)

        val imageIndex = children.indexOfFirst { it.type == FileType.IMAGE }
        val row = imageIndex / dataModel.columnCount
        val column = imageIndex % dataModel.columnCount

        dataModel.getValueAt(row, column)
        dataModel.getValueAt(row, column)
        Thread.sleep(1000)

        verify(model).onLoadContentData(LoadContentDataEvent(state, children[imageIndex], UISizes.smallPreviewSize))
    }
}
