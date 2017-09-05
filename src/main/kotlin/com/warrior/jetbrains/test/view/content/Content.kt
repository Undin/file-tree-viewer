package com.warrior.jetbrains.test.view.content

import com.warrior.jetbrains.test.model.FileInfo

sealed class Content

class SingleFile(val file: FileInfo): Content()
class FileList(val files: List<FileInfo>): Content()
