package com.warrior.jetbrains.test.model

import com.warrior.jetbrains.test.view.TEXT_PREVIEW_LINES
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.awt.Image
import java.awt.Transparency
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.util.concurrent.*
import javax.imageio.ImageIO

object ContentLoader {

    private val logger: Logger = LogManager.getLogger(javaClass)

    private val executor: ExecutorService = Executors.newFixedThreadPool(4)

    fun loadImage(fileInfo: FileInfo, maxSize: Int, callback: (Image?) -> Unit): Future<*> = executor.submit {
        val path = fileInfo.path
        logger.debug("loadImage: $path")
        val image = try {
            val image = ImageIO.read(File(path))
            val size = maxOf(image.width, image.height)
            if (size <= maxSize) {
                image
            } else {
                val scale = size / maxSize.toDouble()
                val scaledWidth = (image.width / scale).toInt()
                val scaledHeight = (image.height / scale).toInt()
                // Copy scaled image to new BufferedImage
                // to force execution of scale operation in background thread
                // because 'Image.getScaledInstance' scales image lazily.
                val lazyScaledImage = image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH)
                val type = if (image.transparency == Transparency.OPAQUE)
                    BufferedImage.TYPE_INT_RGB else BufferedImage.TYPE_INT_ARGB
                val scaledImage = BufferedImage(scaledWidth, scaledHeight, type)
                val g2 = scaledImage.createGraphics();
                g2.drawImage(lazyScaledImage, 0, 0, scaledWidth, scaledHeight, null)
                g2.dispose()
                scaledImage
            }
        } catch (e: IOException) {
            logger.error("Failed to load image $path", e)
            null
        }
        callback(image)
    }

    fun loadText(fileInfo: FileInfo, callback: (String?) -> Unit): Future<*> = executor.submit {
        val path = fileInfo.path
        logger.debug("loadText: $path")
        val text = try {
            File(path).useLines { lines ->
                val iterator = lines.iterator()
                var linesCount = 0
                val builder = StringBuilder()
                while (iterator.hasNext() && linesCount < TEXT_PREVIEW_LINES) {
                    builder.append(iterator.next()).append("\n")
                    linesCount++
                }
                if (iterator.hasNext()) {
                    builder.append("...")
                }
                builder.toString()
            }
        } catch (e: IOException) {
            logger.debug("Failed to load text from $path", e)
            null
        }
        callback(text)
    }
}
