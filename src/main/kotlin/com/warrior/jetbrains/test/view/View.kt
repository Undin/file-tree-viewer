package com.warrior.jetbrains.test.view

import org.apache.commons.vfs2.FileObject

interface View {
    fun addRoot(root: FileObject)
    fun setContentData(data: List<FileObject>)
}
