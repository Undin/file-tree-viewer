package com.warrior.jetbrains.test.view.tree

import com.warrior.jetbrains.test.getChildrenSync
import com.warrior.jetbrains.test.model.filter.ExtensionFileFilter
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
        val children = model.getChildrenSync(root)
        treeModel.setNodeChildren(tree, children.map(::FileTreeNode))

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
        val children = model.getChildrenSync(root)
        treeModel.setNodeChildren(tree, children.map(::FileTreeNode))
        treeModel.applyFilter(ExtensionFileFilter("zip"))

        checkTree(tree, tree("root") {
            node("archive.zip")
            node("dir")
            node("outerArchive.zip")
        })
    }

    @Test
    fun `apply several filters`() {
        val children = model.getChildrenSync(root)
        treeModel.setNodeChildren(tree, children.map(::FileTreeNode))

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

        val children = model.getChildrenSync(root)
        treeModel.setNodeChildren(tree, children.map(::FileTreeNode))

        checkTree(tree, tree("root") {
            node("dir")
            node("file.txt")
        })
    }

    @Test
    fun `applyFilter must filter all tree`() {

        fun loadFullTree(tree: FileTreeNode) {
            val children = model.getChildrenSync(tree.userObject).map(::FileTreeNode)
            treeModel.setNodeChildren(tree, children)
            children.forEach { loadFullTree(it) }
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
}
