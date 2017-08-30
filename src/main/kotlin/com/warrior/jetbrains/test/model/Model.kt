package com.warrior.jetbrains.test.model

import com.warrior.jetbrains.test.isDirectory
import com.warrior.jetbrains.test.isZip
import com.warrior.jetbrains.test.presenter.Presenter
import org.apache.commons.vfs2.FileObject
import org.apache.commons.vfs2.VFS
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.IOException

class Model(private val presenter: Presenter) {

    private val logger: Logger = LogManager.getLogger(javaClass)

    fun getChildren(file: FileObject): List<FileObject> {
        logger.debug("getChildren. path: $file")
        if (file.isZip) {
            val zipChildren = getArchiveChildren(file)
            if (zipChildren != null) {
                return zipChildren
            }
        }

        if (!file.isDirectory) return emptyList()
        return try {
            file.children.filter { !it.isHidden }
        } catch (e: IOException) {
            logger.error("Failed to get children of $file", e)
            emptyList()
        }
    }

    private fun getArchiveChildren(zipFile: FileObject): List<FileObject>? {
        val extension = zipFile.name.extension
        check(extension == "zip" || extension == "jar") {
            "Archive file must be zip or jar"
        }
        val archiveFile = VFS.getManager().resolveFile("$extension:$zipFile")
        return getChildren(archiveFile)
    }
}
