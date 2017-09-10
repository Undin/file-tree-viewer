package com.warrior.jetbrains.test.integration

import com.warrior.jetbrains.test.any
import com.warrior.jetbrains.test.event.*
import com.warrior.jetbrains.test.getChildrenSync
import com.warrior.jetbrains.test.model.FileInfoLoader
import com.warrior.jetbrains.test.resourceFile
import com.warrior.jetbrains.test.ui.tree.FileTreeNode
import org.junit.Test
import org.mockito.Mockito.atLeastOnce
import org.mockito.Mockito.verify

class GeneralTest : BaseIntegrationTest() {

    @Test
    fun `add roots on start`() {
        StartEvent.post()
        Thread.sleep(100)

        verify(view, atLeastOnce()).addRoot(any())
    }

    @Test
    fun `set children`() {
        val root = FileInfoLoader.resourceFile("root", true)
        val node = FileTreeNode(root)
        PreNodeExpandEvent(node).post()
        Thread.sleep(1000)

        verify(view).onStartLoadingChildren(StartLoadingChildrenEvent(node))
        val children = FileInfoLoader.getChildrenSync(root)
        verify(view).setNodeChildren(ChildrenLoadedEvent(node, children))
    }
}
