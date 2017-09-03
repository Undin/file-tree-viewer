package com.warrior.jetbrains.test

import com.warrior.jetbrains.test.model.FileInfo
import com.warrior.jetbrains.test.model.Model
import org.mockftpserver.fake.FakeFtpServer
import org.mockftpserver.fake.UserAccount
import org.mockftpserver.fake.filesystem.*
import org.mockito.Mockito
import java.nio.file.Paths

fun Model.getChildrenSync(file: FileInfo): List<FileInfo> {
    var children: List<FileInfo>? = null
    getChildrenAsync(file) { children = it }.get()
    return children ?: error("Result list is supposed to be not empty")
}

fun Model.localFile(path: String): FileInfo {
    val realPath = Paths.get(javaClass.classLoader.getResource(path).path)
    return getLocalFile(realPath) ?: error("Failed to create 'FileInfo' from $path")
}

inline fun <reified T> mock(): T = Mockito.mock(T::class.java)

fun <T> any(): T {
    Mockito.any<T>()
    return uninitialized()
}

fun <T> any(clazz: Class<T>): T {
    Mockito.any(clazz)
    return uninitialized()
}

// hack to use Mockito.any() from kotlin
// see https://medium.com/@elye.project/befriending-kotlin-and-mockito-1c2e7b0ef791
private fun <T> uninitialized(): T = null as T

inline fun ftp(block: FtpServerBuilder.() -> Unit): FakeFtpServer {
    val ftpServer = FtpServerBuilder().apply(block).ftpServer
    ftpServer.serverControlPort = 0
    ftpServer.start()
    return ftpServer
}


class FtpServerBuilder {
    val ftpServer: FakeFtpServer = FakeFtpServer()

    inline fun content(block: FileSystemBuilder.() -> Unit) {
        val builder = FileSystemBuilder()
        builder.block()
        ftpServer.fileSystem = builder.fileSystem
    }

    fun user(name: String, password: String, userDir: String = "/") =
            ftpServer.addUserAccount(UserAccount(name, password, userDir))
}

class FileSystemBuilder(val fileSystem: FileSystem = UnixFakeFileSystem()) {
    fun file(path: String) = fileSystem.add(FileEntry(path))
    fun dir(path: String) = fileSystem.add(DirectoryEntry(path))
}
