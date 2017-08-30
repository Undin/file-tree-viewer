package com.warrior.jetbrains.test.view

import org.apache.commons.vfs2.FileObject

interface View {
    fun setContentData(data: List<FileObject>)
}
