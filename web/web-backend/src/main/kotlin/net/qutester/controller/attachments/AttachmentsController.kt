package net.qutester.controller.attachments

import com.testerum.file_repository.AttachmentFileRepositoryService
import com.testerum.file_repository.model.KnownPath
import com.testerum.model.file.Attachment
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.repository.enums.FileType
import org.springframework.web.bind.annotation.*
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.OutputStream
import javax.imageio.ImageIO
import javax.servlet.http.HttpServletResponse


@RestController
@RequestMapping("/attachments")
class AttachmentsController(private val attachmentFileRepositoryService: AttachmentFileRepositoryService) {


    @RequestMapping(method = [RequestMethod.GET], path = [""], params = ["path"])
    @ResponseBody
    fun getAttachmentFile(@RequestParam(value = "path") path: String,
                          @RequestParam(value = "thumbnail", required = false) thumbnail: Boolean = false,
                          response: HttpServletResponse) {
        val knownPath = KnownPath(Path.createInstance(path), FileType.FEATURE)
        val attachmentDetails = attachmentFileRepositoryService.getAttachmentDetailsFromPath(knownPath)

        response.reset()
        response.bufferSize = DEFAULT_BUFFER_SIZE

        if (attachmentDetails?.mimeType != null
                && attachmentDetails.mimeType!!.startsWith("image/")
                && thumbnail) {
            response.setHeader("Content-Type", "image/jpeg")
            getImageThumbnail(knownPath, response.outputStream)
            return
        }

        if (attachmentDetails?.mimeType != null) {
            response.setHeader("Content-Type", attachmentDetails.mimeType)
            response.setHeader("Content-Disposition", "inline; filename=" + attachmentDetails.path.fileName + "." + attachmentDetails.path.fileExtension)
        }

        response.outputStream.write(
                attachmentFileRepositoryService.getAttachment(knownPath)
        )
    }

    private fun getImageThumbnail(knownPath: KnownPath, outputStream: OutputStream) {
        val byteArrayInputStream = ByteArrayInputStream(attachmentFileRepositoryService.getAttachment(knownPath))
        val sourceImage = ImageIO.read(byteArrayInputStream)

        val resizedImage = resizeImage(sourceImage, 200, 150)
        ImageIO.write(resizedImage, "jpg", outputStream)
    }

    private fun resizeImage(sourceImage: BufferedImage, targetWidth: Int, targetHeight: Int): BufferedImage {
        val determineImageScale = determineImageScale(sourceImage.width, sourceImage.height, targetWidth, targetHeight)

        return scaleImage(sourceImage, determineImageScale)
    }

    private fun determineImageScale(sourceWidth: Int, sourceHeight: Int, targetWidth: Int, targetHeight: Int): Double {
        val scalex = targetWidth.toDouble() / sourceWidth
        val scaley = targetHeight.toDouble() / sourceHeight

        return Math.min(scalex, scaley)
    }

    private fun scaleImage(sourceImage: BufferedImage, scaledWidth: Double): BufferedImage {
        val scaledImage = sourceImage.getScaledInstance((sourceImage.width * scaledWidth).toInt(), (sourceImage.height * scaledWidth).toInt(), Image.SCALE_SMOOTH)
        val bufferedImage = BufferedImage(
                scaledImage.getWidth(null),
                scaledImage.getHeight(null),
                BufferedImage.TYPE_INT_RGB
        )
        val g = bufferedImage.createGraphics()
        g.drawImage(scaledImage, 0, 0, null)
        g.dispose()
        return bufferedImage
    }

    @RequestMapping(method = [RequestMethod.DELETE], path = [""], params = ["path"])
    fun delete(@RequestParam(value = "path") path: String) {
        attachmentFileRepositoryService.delete(
                KnownPath(Path.createInstance(path), FileType.FEATURE)
        )
    }

    @RequestMapping(method = [RequestMethod.GET], path = ["/info"], params = ["path"])
    @ResponseBody
    fun getAttachmentsInfo(@RequestParam(value = "path") path: String): List<Attachment> {
        val knownPath = KnownPath(Path.createInstance(path), FileType.FEATURE)

        return attachmentFileRepositoryService.getAttachmentsDetailsFromPath(knownPath)
    }

}
