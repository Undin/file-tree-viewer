package com.warrior.jetbrains.test.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Test
import org.mockftpserver.fake.FakeFtpServer
import org.mockftpserver.fake.UserAccount
import org.mockftpserver.fake.filesystem.*

class FtpModelTest : BaseModelTest() {

    private lateinit var ftpServer: FakeFtpServer

    @After
    fun tearDown() {
        ftpServer.stop()
    }

    @Test
    fun `ftp server`() {
        ftpServer = createFakeFtpServer {
            dir("/dir")
            file("/file.txt")
        }
        val ftpObject = localFtpServerRoot(ftpServer.serverControlPort, USER1, PASSWORD)
                ?: error("Failed to create ftp server object")
        checkChildren(ftpObject, "dir", "file.txt")
    }

    @Test
    fun `show only user directory`() {
        ftpServer = createFakeFtpServer("/user1") {
            dir("/user1")
            dir("/user2")
            file("/user1/dir")
            file("/user1/file.txt")
        }
        val ftpObject = localFtpServerRoot(ftpServer.serverControlPort, USER1, PASSWORD)
                ?: error("Failed to create ftp server object")
        checkChildren(ftpObject, "dir", "file.txt")
    }

    @Test
    fun `wrong credentials`() {
        ftpServer = createFakeFtpServer { file("/file.txt") }
        val ftpObject = localFtpServerRoot(ftpServer.serverControlPort, USER1, WRONG_PASSWORD)
        assertThat(ftpObject).isNull()
    }

    @Test
    fun `host with schema prefix`() {
        ftpServer = createFakeFtpServer { file("/file.txt") }
        val ftpObject = model.createFtpServerRoot("ftp://localhost:${ftpServer.serverControlPort}", USER1, PASSWORD.toCharArray())
        assertThat(ftpObject).isNotNull()
    }

    @Test
    fun `username escaping`() {
        ftpServer = createFakeFtpServer { file("/file.txt") }
        val ftpObject = localFtpServerRoot(ftpServer.serverControlPort, USER2, PASSWORD)
        assertThat(ftpObject).isNotNull()
    }

    private fun localFtpServerRoot(port: Int, username: String, password: String): FileInfo? =
            model.createFtpServerRoot("localhost:$port", username, password.toCharArray())

    private fun createFakeFtpServer(homeDir: String = "/", block: FileSystemBuilder.() -> Unit): FakeFtpServer {
        val fakeFtpServer = FakeFtpServer()
        // use any free port
        fakeFtpServer.serverControlPort = 0
        fakeFtpServer.fileSystem = fileSystem(block)

        fakeFtpServer.addUserAccount(UserAccount(USER1, PASSWORD, homeDir))
        fakeFtpServer.addUserAccount(UserAccount(USER2, PASSWORD, homeDir))
        fakeFtpServer.start()
        return fakeFtpServer
    }

    private class FileSystemBuilder(val fileSystem: FileSystem = UnixFakeFileSystem()) {
        fun file(path: String) = fileSystem.add(FileEntry(path))
        fun dir(path: String) = fileSystem.add(DirectoryEntry(path))
    }

    private fun fileSystem(block: FileSystemBuilder.() -> Unit): FileSystem {
        val builder = FileSystemBuilder()
        builder.block()
        return builder.fileSystem
    }

    companion object {
        private const val USER1 = "user"
        private const val USER2 = "user/user"
        private const val PASSWORD = "password"
        private const val WRONG_PASSWORD = "wrong_password"
    }
}
