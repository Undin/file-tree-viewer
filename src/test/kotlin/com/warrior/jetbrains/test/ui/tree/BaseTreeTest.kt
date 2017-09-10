package com.warrior.jetbrains.test.ui.tree

import com.warrior.jetbrains.test.model.FileInfo
import com.warrior.jetbrains.test.model.FileInfoLoader
import com.warrior.jetbrains.test.resourceFile
import org.assertj.core.api.Assertions
import org.junit.Before
import javax.swing.tree.DefaultMutableTreeNode

abstract class BaseTreeTest {

    protected lateinit var root: FileInfo
    protected lateinit var tree: FileTreeNode

    @Before
    fun setUp() {
        root = FileInfoLoader.resourceFile("root")
        tree = FileTreeNode(root)
    }

    @Suppress("UNCHECKED_CAST")
    protected fun checkTree(actualTree: DefaultMutableTreeNode, expectedTree: DefaultMutableTreeNode) {
        Assertions.assertThat(actualTree.toString()).isEqualTo(expectedTree.toString())

        val actualChildren = actualTree.children().toList() as List<DefaultMutableTreeNode>
        val expectedChildren = expectedTree.children().toList() as List<DefaultMutableTreeNode>
        Assertions.assertThat(actualChildren.size).isEqualTo(expectedChildren.size)

        for ((actualChild, expectedChild) in actualChildren.zip(expectedChildren)) {
            checkTree(actualChild, expectedChild)
        }
    }

    protected inline fun tree(name: String, block: TestTreeBuilder.() -> Unit): DefaultMutableTreeNode {
        val root = DefaultMutableTreeNode(name)
        TestTreeBuilder(root).block()
        return root
    }

    protected class TestTreeBuilder(val root: DefaultMutableTreeNode) {

        fun node(name: String, block: (TestTreeBuilder.() -> Unit)? = null) {
            val node = DefaultMutableTreeNode(name)
            if (block != null) {
                TestTreeBuilder(node).block()
            }
            root.add(node)
        }
    }
}
