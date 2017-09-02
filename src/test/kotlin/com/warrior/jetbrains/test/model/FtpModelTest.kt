package com.warrior.jetbrains.test.model

import com.warrior.jetbrains.test.ftp
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Test
import org.mockftpserver.fake.FakeFtpServer

class FtpModelTest : BaseModelTest() {

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
        checkChildren(ftpObject, "dir", "file.txt")
    }

    @Test
    fun `show only user directory`() {
        ftpServer = ftp {
            content {
                dir("/user1")
                dir("/user2")
                file("/user1/dir")
                file("/user1/file.txt")
            }
            user(USER1, PASSWORD, "/user1")
        }

        val ftpObject = localFtpServerRoot(ftpServer.serverControlPort, USER1, PASSWORD)
                ?: error("Failed to create ftp server object")
        checkChildren(ftpObject, "dir", "file.txt")
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
        val ftpObject = model.createFtpServerRoot("ftp://localhost:${ftpServer.serverControlPort}", USER1, PASSWORD.toCharArray())
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
            model.createFtpServerRoot("localhost:$port", username, password.toCharArray())

    companion object {
        private const val USER1 = "user"
        private const val USER2 = "user/user"
        private const val PASSWORD = "password"
        private const val WRONG_PASSWORD = "wrong_password"
    }
}
