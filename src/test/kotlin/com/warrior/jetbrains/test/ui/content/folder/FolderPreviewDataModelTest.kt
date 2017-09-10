package com.warrior.jetbrains.test.ui.content.folder

import com.warrior.jetbrains.test.getChildrenSync
import com.warrior.jetbrains.test.model.FileInfo
import com.warrior.jetbrains.test.model.FileInfoLoader
import com.warrior.jetbrains.test.model.filter.AnyFileFilter
import com.warrior.jetbrains.test.model.filter.ExtensionFileFilter
import com.warrior.jetbrains.test.resourceFile
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class FolderPreviewDataModelTest {

    private val root: FileInfo = FileInfoLoader.resourceFile("root", true)
    private val children: List<FileInfo> = FileInfoLoader.getChildrenSync(root)

    @Test
    fun `set file list`() {
        val model = FolderPreviewDataModel(children, 0, AnyFileFilter)

        checkModel(model, children.size)
    }

    @Test
    fun `set file list with filter`() {
        val filter = ExtensionFileFilter("zip")
        val filteredChildren = children.filter(filter::accept)

        val model = FolderPreviewDataModel(children, 0, filter)

        checkModel(model, filteredChildren.size)
    }

    @Test
    fun `apply filter`() {
        val filter = ExtensionFileFilter("zip")
        val filteredChildren = children.filter(filter::accept)

        val model = FolderPreviewDataModel(children, 0, AnyFileFilter)
        model.applyFilter(filter)

        checkModel(model, filteredChildren.size)
    }

    private fun checkModel(model: FolderPreviewDataModel, expectedFileCount: Int) {
        for (row in 0 until model.rowCount) {
            for (column in 0 until model.columnCount) {
                val flatIndex = row * model.columnCount + column
                val value = model.getValueAt(row, column)
                if (flatIndex < expectedFileCount) {
                    assertThat(value).isNotNull()
                } else {
                    assertThat(value).isNull()
                }
            }
        }
    }
}
