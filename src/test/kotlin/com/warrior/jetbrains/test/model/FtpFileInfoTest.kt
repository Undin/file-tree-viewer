package com.warrior.jetbrains.test.model

import com.warrior.jetbrains.test.ftp
import com.warrior.jetbrains.test.model.FileLocation.FTP
import com.warrior.jetbrains.test.model.FileType.FOLDER
import com.warrior.jetbrains.test.model.FileType.TEXT
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Test
import org.mockftpserver.fake.FakeFtpServer

class FtpFileInfoTest : BaseFileInfoLoaderTest() {

    private lateinit var ftpServer: FakeFtpServer

    @After
    fun tearDown() {
        ftpServer.stop()
    }

    @Test
    fun `ftp server`() {
        ftpServer = ftp {
            content {
                dir("/dir")
                file("/file.txt")
            }
            user(USER1, PASSWORD)
        }

        val ftpObject = localFtpServerRoot(ftpServer.serverControlPort, USER1, PASSWORD)
                ?: error("Failed to create ftp server object")
        checkChildren(
                ftpObject,
                file("dir", FTP, FOLDER),
                file("file.txt", FTP, TEXT)
        )
    }

    @Test
    fun `show only user directory`() {
        ftpServer = ftp {
            content {
                dir("/user1")
                dir("/user2")
                dir("/user1/dir")
                file("/user1/file.txt")
            }
            user(USER1, PASSWORD, "/user1")
        }

        val ftpObject = localFtpServerRoot(ftpServer.serverControlPort, USER1, PASSWORD)
                ?: error("Failed to create ftp server object")
        checkChildren(
                ftpObject,
                file("dir", FTP, FOLDER),
                file("file.txt", FTP, TEXT)
        )
    }

    @Test
    fun `wrong credentials`() {
        ftpServer = ftp {
            content { file("/file.txt") }
            user(USER1, PASSWORD)
        }
        val ftpObject = localFtpServerRoot(ftpServer.serverControlPort, USER1, WRONG_PASSWORD)
        assertThat(ftpObject).isNull()
    }

    @Test
    fun `host with schema prefix`() {
        ftpServer = ftp {
            content { file("/file.txt") }
            user(USER1, PASSWORD)
        }
        val ftpObject = FileInfoLoader.createFtpServerRoot("ftp://localhost:${ftpServer.serverControlPort}", USER1, PASSWORD.toCharArray())
        assertThat(ftpObject).isNotNull()
    }

    @Test
    fun `username escaping`() {
        ftpServer = ftp {
            content { file("/file.txt") }
            user(USER2, PASSWORD)
        }
        val ftpObject = localFtpServerRoot(ftpServer.serverControlPort, USER2, PASSWORD)
        assertThat(ftpObject).isNotNull()
    }

    private fun localFtpServerRoot(port: Int, username: String, password: String): FileInfo? =
            FileInfoLoader.createFtpServerRoot("localhost:$port", username, password.toCharArray())

    companion object {
        private const val USER1 = "user"
        private const val USER2 = "user/user"
        private const val PASSWORD = "password"
        private const val WRONG_PASSWORD = "wrong_password"
    }
}
