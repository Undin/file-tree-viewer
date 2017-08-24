package com.warrior.jetbrains.test

import java.nio.file.Path
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTree
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeSelectionModel

class FileViewerPanel(path: Path) : JPanel() {

    init {
        val root = node(path)
        val fileTree = JTree(DefaultTreeModel(root, true))
        fileTree.selectionModel.selectionMode = TreeSelectionModel.SINGLE_TREE_SELECTION
        val treeView = JScrollPane(fileTree)
        add(treeView)
    }

    private fun node(path: Path): FileTreeNode = FileTreeNode(FileNodeData(path))
}
