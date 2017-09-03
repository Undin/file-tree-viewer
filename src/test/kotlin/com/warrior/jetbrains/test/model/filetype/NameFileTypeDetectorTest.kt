package com.warrior.jetbrains.test.model.filetype

import com.warrior.jetbrains.test.model.FileType
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class NameFileTypeDetectorTest {

    @Test
    fun testText() = doTest(TEXT_EXTENSIONS, FileType.TEXT)

    @Test
    fun testImages() = doTest(IMAGES_EXTENSIONS, FileType.IMAGE)

    @Test
    fun testAudio() = doTest(AUDIO_EXTENSIONS, FileType.AUDIO)

    @Test
    fun testVideo() = doTest(VIDEO_EXTENSIONS, FileType.VIDEO)

    @Test
    fun testArchive() = doTest(ARCHIVE_EXTENSIONS, FileType.ARCHIVE)

    private fun doTest(extensions: List<String>, expectedFileType: FileType) {
        val detector = NameFileTypeDetector()
        for (ext in extensions) {
            assertThat(detector.fileType("name.${ext.toLowerCase()}"))
                    .isEqualTo(expectedFileType)
            assertThat(detector.fileType("name.${ext.toUpperCase()}"))
        }
    }

    companion object {
        private val TEXT_EXTENSIONS = listOf("txt", "html", "xml", "csv")
        private val IMAGES_EXTENSIONS = listOf("jpg", "jpeg", "png", "gif")
        private val ARCHIVE_EXTENSIONS = listOf("zip", "jar")
        private val AUDIO_EXTENSIONS = listOf("mp3", "flac", "wav", "aac")
        private val VIDEO_EXTENSIONS = listOf("avi", "mkv", "mov", "mp4", "m4v")
    }
}
