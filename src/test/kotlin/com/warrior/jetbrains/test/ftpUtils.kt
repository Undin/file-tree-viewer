package com.warrior.jetbrains.test

import com.warrior.jetbrains.test.model.*
import org.mockftpserver.fake.FakeFtpServer
import org.mockftpserver.fake.UserAccount
import org.mockftpserver.fake.filesystem.*

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

fun resolveLocalFtpServer(port: Int, username: String, password: String, name: String? = null): FileInfo {
    val result = FileInfoLoader.resolveFtpServerSync("localhost:$port", username, password.toCharArray(), name)
    return when (result) {
        is Ok -> result.value
        is Err -> error("Failed to create ftp server object")
    }
}
