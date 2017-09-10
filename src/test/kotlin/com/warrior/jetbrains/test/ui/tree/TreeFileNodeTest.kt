package com.warrior.jetbrains.test.ui.tree

import com.warrior.jetbrains.test.getChildrenSync
import com.warrior.jetbrains.test.model.FileInfo
import com.warrior.jetbrains.test.model.FileInfoLoader
import com.warrior.jetbrains.test.model.filter.AnyFileFilter
import com.warrior.jetbrains.test.model.filter.ExtensionFileFilter
import org.junit.Test

class TreeFileNodeTest: BaseTreeTest() {

    @Test
    fun `set loading state`() {
        tree.setLoadingState()

        checkTree(tree, tree("root") {
            node("Loading...")
        })
    }

    @Test
    fun `set children with any filter`() {
        val children = FileInfoLoader.getChildrenSync(root)
        tree.setUnfilteredChildren(children.map(::FileTreeNode), AnyFileFilter)

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
    fun `set children with extension filter`() {
        val children = FileInfoLoader.getChildrenSync(root)
        tree.setUnfilteredChildren(children.map(::FileTreeNode), ExtensionFileFilter("txt"))

        checkTree(tree, tree("root") {
            node("dir")
            node("file.txt")
        })
    }

    @Test
    fun `apply filter`() {
        val children = FileInfoLoader.getChildrenSync(root)
        tree.setUnfilteredChildren(children.map(::FileTreeNode), AnyFileFilter)
        tree.applyFilter(ExtensionFileFilter("zip"))

        checkTree(tree, tree("root") {
            node("archive.zip")
            node("dir")
            node("outerArchive.zip")
        })
    }

    @Test
    fun `apply several filters`() {
        val children = FileInfoLoader.getChildrenSync(root)
        tree.setUnfilteredChildren(children.map(::FileTreeNode), AnyFileFilter)

        tree.applyFilter(ExtensionFileFilter("zip"))
        checkTree(tree, tree("root") {
            node("archive.zip")
            node("dir")
            node("outerArchive.zip")
        })

        tree.applyFilter(ExtensionFileFilter("png"))
        checkTree(tree, tree("root") {
            node("dir")
            node("image.png")
        })
    }

    @Test
    fun `applyFilter must filter only children`() {

        fun loadFullTree(file: FileInfo): FileTreeNode {
            val children = FileInfoLoader.getChildrenSync(file)
                    .map { loadFullTree(it) }
            return FileTreeNode(file).apply { setUnfilteredChildren(children, AnyFileFilter) }
        }

        val tree = loadFullTree(root)
        tree.applyFilter(ExtensionFileFilter("png"))
        checkTree(tree, tree("root") {
            node("dir") {
                node("innerFile.txt")
                node("innerImage.png")
            }
            node("image.png")
        })
    }
}
