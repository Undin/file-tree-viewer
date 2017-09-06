package com.warrior.jetbrains.test.view.content

import com.warrior.jetbrains.test.model.FileInfo
import java.awt.Image

sealed class Content

object Empty : Content() {
    override fun toString(): String = "Empty"
}
data class SingleFile(val file: FileInfo): Content() {
    override fun toString(): String = "SingleFile(file=$file)"
}
data class FileList(val files: List<FileInfo>): Content() {
    override fun toString(): String = "FileList(files=$files)"
}

sealed class ContentData(val file: FileInfo)

class Text(file: FileInfo, val text: String) : ContentData(file) {
    override fun toString(): String = "Text(file=$file)"
}
class Image(file: FileInfo, val image: Image) : ContentData(file) {
    override fun toString(): String = "Image(file=$file)"
}
