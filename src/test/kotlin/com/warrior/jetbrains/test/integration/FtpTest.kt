package com.warrior.jetbrains.test.integration

import com.warrior.jetbrains.test.any
import com.warrior.jetbrains.test.event.*
import com.warrior.jetbrains.test.ftp
import com.warrior.jetbrains.test.model.Err
import com.warrior.jetbrains.test.model.FileInfoLoader
import com.warrior.jetbrains.test.model.Ok
import com.warrior.jetbrains.test.resolveLocalFtpServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockftpserver.fake.FakeFtpServer
import org.mockito.Mockito.never
import org.mockito.Mockito.verify

class FtpTest : BaseIntegrationTest() {

    private lateinit var ftpServer: FakeFtpServer

    @Before
    fun setUpFtp() {
        ftpServer = ftp {
            content {
                dir("/dir")
                file("/file.txt")
            }
            user(USER, PASSWORD)
        }
    }

    @After
    fun tearDownFtp() {
        ftpServer.stop()
    }

    @Test
    fun `add ftp server`() {
        AddNewFtpServerEvent("localhost:${ftpServer.serverControlPort}", USER, PASSWORD.toCharArray(), null).post()
        val ftpRoot = resolveLocalFtpServer(ftpServer.serverControlPort, USER, PASSWORD)

        Thread.sleep(1000)
        verify(view).onFtpServerResolved(FtpServerResolvedEvent(Ok(ftpRoot)))
        verify(view).addRoot(AddRootEvent(ftpRoot))
    }

    @Test
    fun `ftp server resolve failed 1`() {
        AddNewFtpServerEvent("", "", PASSWORD.toCharArray(), null).post()

        Thread.sleep(1000)
        verify(view).onFtpServerResolved(FtpServerResolvedEvent(Err(FileInfoLoader.INVALID_URI)))
        verify(view, never()).addRoot(any())
    }

    @Test
    fun `ftp server resolve failed 2`() {
        AddNewFtpServerEvent("localhost:${ftpServer.serverControlPort}", USER + "1", PASSWORD.toCharArray(), null).post()

        Thread.sleep(1000)
        verify(view).onFtpServerResolved(FtpServerResolvedEvent(Err(FileInfoLoader.RESOLVE_FAILED)))
        verify(view, never()).addRoot(any())
    }

    companion object {
        private const val USER = "user"
        private const val PASSWORD = "password"
    }
}
