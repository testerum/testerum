package com.testerum.common_jdk

import java.awt.Image
import java.awt.image.BufferedImage

fun resizeImage(sourceImage: BufferedImage, targetWidth: Int, targetHeight: Int): BufferedImage {
    val determineImageScale = determineImageScale(
            sourceImage.width,
            sourceImage.height,
            targetWidth,
            targetHeight
    )

    return scaleImage(sourceImage, determineImageScale)
}

private fun determineImageScale(sourceWidth: Int, sourceHeight: Int, targetWidth: Int, targetHeight: Int): Double {
    val scalex = targetWidth.toDouble() / sourceWidth
    val scaley = targetHeight.toDouble() / sourceHeight

    return Math.min(scalex, scaley)
}

private fun scaleImage(sourceImage: BufferedImage, scaledWidth: Double): BufferedImage {
    val newWidth = (sourceImage.width * scaledWidth).toInt()
    val newHeight = (sourceImage.height * scaledWidth).toInt()

    val scaledImage = sourceImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH)
    val resizedImage = BufferedImage(
            scaledImage.getWidth(null),
            scaledImage.getHeight(null),
            BufferedImage.TYPE_INT_RGB
    )

    val graphics = resizedImage.createGraphics()
    try {
        graphics.drawImage(scaledImage, 0, 0, null)
    } finally {
        graphics.dispose()
    }

    return resizedImage
}
