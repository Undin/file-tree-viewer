package com.warrior.jetbrains.test.ui.tree

import com.warrior.jetbrains.test.getChildrenSync
import com.warrior.jetbrains.test.model.FileInfoLoader
import com.warrior.jetbrains.test.model.filter.ExtensionFileFilter
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test

class TreeFileModelTest : BaseTreeTest() {

    private lateinit var treeModel: FileTreeModel

    @Before
    fun setUp2() {
        treeModel = FileTreeModel(tree)
    }

    @Test
    fun `set loading state`() {
        treeModel.setLoadingState(tree)
        checkTree(tree, tree("root") {
            node("Loading...")
        })
    }

    @Test
    fun `set children`() {
        val children = FileInfoLoader.getChildrenSync(root)
        treeModel.setNodeChildren(tree, children)

        checkTree(tree, tree("root") {
            node("archive.zip")
            node("dir")
            node("file.txt")
            node("image.png")
            node("outerArchive.zip")
            node("unknown_file")
        })
    }

    @Test
    fun `apply filter`() {
        val children = FileInfoLoader.getChildrenSync(root)
        treeModel.setNodeChildren(tree, children)
        treeModel.applyFilter(ExtensionFileFilter("zip"))

        checkTree(tree, tree("root") {
            node("archive.zip")
            node("dir")
            node("outerArchive.zip")
        })
    }

    @Test
    fun `apply several filters`() {
        val children = FileInfoLoader.getChildrenSync(root)
        treeModel.setNodeChildren(tree, children)

        treeModel.applyFilter(ExtensionFileFilter("zip"))
        checkTree(tree, tree("root") {
            node("archive.zip")
            node("dir")
            node("outerArchive.zip")
        })

        treeModel.applyFilter(ExtensionFileFilter("png"))
        checkTree(tree, tree("root") {
            node("dir")
            node("image.png")
        })
    }

    @Test
    fun `set children after applying filter`() {
        treeModel.applyFilter(ExtensionFileFilter("txt"))

        val children = FileInfoLoader.getChildrenSync(root)
        treeModel.setNodeChildren(tree, children)

        checkTree(tree, tree("root") {
            node("dir")
            node("file.txt")
        })
    }

    @Test
    fun `applyFilter must filter all tree`() {

        fun loadFullTree(tree: FileTreeNode) {
            val children = FileInfoLoader.getChildrenSync(tree.userObject)
            treeModel.setNodeChildren(tree, children)
            for (child in tree.children()) {
                if (child !is FileTreeNode) continue
                loadFullTree(child)
            }
        }

        loadFullTree(tree)
        treeModel.applyFilter(ExtensionFileFilter("png"))
        checkTree(tree, tree("root") {
            node("dir") {
                node("innerImage.png")
            }
            node("image.png")
        })
    }

    @Test
    fun `get node by file`() {
        val files = FileInfoLoader.getChildrenSync(root)
        treeModel.setNodeChildren(tree, files)

        val nodeFromModel = treeModel.getNode(files[0])
        val nodeFromTree = tree.firstChild as FileTreeNode
        Assertions.assertThat(nodeFromModel).isSameAs(nodeFromTree)
    }
}
