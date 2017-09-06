package com.warrior.jetbrains.test.view.content

import com.warrior.jetbrains.test.model.FileInfo
import java.awt.Image

sealed class Content

data class SingleFile(val file: FileInfo): Content()
data class FileList(val files: List<FileInfo>): Content()

sealed class ContentData(val file: FileInfo)

class Text(file: FileInfo, val text: String) : ContentData(file) {
    override fun toString(): String = "Text(file=$file)"
}
class Image(file: FileInfo, val image: Image) : ContentData(file) {
    override fun toString(): String = "Image(file=$file)"
}
