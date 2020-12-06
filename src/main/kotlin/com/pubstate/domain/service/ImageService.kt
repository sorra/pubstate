package com.pubstate.domain.service

import com.pubstate.domain.entity.Image
import com.pubstate.domain.entity.User
import com.pubstate.domain.permission.ImagePermission
import com.pubstate.util.Settings
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

@Service
class ImageService {

  /**
   * @return file name
   */
  @Throws(IOException::class)
  fun upload(userId: String, file: MultipartFile, isAvatar: Boolean): String {
    return saveFile(userId, file, isAvatar)
  }

  @Throws(IOException::class)
  fun multiUpload(userId: String, files: Array<MultipartFile>, isAvatar: Boolean): Collection<String> {
    return files.map { file ->
      saveFile(userId, file, isAvatar)
    }
  }

  fun delete(userId: String, id: String) {
    val image = Image.byId(id)
        ?: return

    ImagePermission(userId, image).canDelete()
    image.delete()
  }

  /**
   * @return file name
   */
  @Throws(IOException::class)
  private fun saveFile(ownerId: String, file: MultipartFile, isAvatar: Boolean): String {
    if (file.size == 0L) {
      throw IllegalArgumentException("Rejected: file is empty!")
    }
    if (file.size > MAX_BYTES) {
      throw IllegalArgumentException("Rejected: allowed max file size is ${MAX_BYTES}MB!")
    }

    val image = Image(User.ref(ownerId), isAvatar)

    // Restrict file suffix, in order to prevent XSS attack
    val fileName = image.id + SUFFIX
    Files.write(Paths.get(folderPath(), fileName), file.bytes, StandardOpenOption.CREATE_NEW)
    log.info("Saved image: {}, original name: {}", fileName, file.name)

    image.save()

    return fileName
  }

  private class FolderManager(folderName: String) {
    val dir: File

    init {
      Paths.get(FILESTORE_ROOT).toFile().also {
        if (!it.exists()) {
          log.warn("FILESTORE_HOME is missing, recreate $FILESTORE_ROOT")
          it.mkdirs()
        }
      }

      dir = Paths.get(FILESTORE_ROOT, folderName).toFile().also {
        if ((it.exists() || it.mkdirs()) && it.canWrite()) {
          log.info("Selected the directory: {}", it)
        } else {
          error("Failed to create the upload directory under " + FILESTORE_ROOT)
        }
      }
    }
  }

  companion object {
    private const val MAX_MBS = 4L
    private const val MAX_BYTES = MAX_MBS * 1024 * 1024
    private const val SUFFIX = ".jpg"

    private val log = LoggerFactory.getLogger(ImageService::class.java)

    private val FILESTORE_ROOT = {
      val value = Settings.getProperty("filestore.root")
          ?: throw IllegalArgumentException("filestore.root is not set!")

      if (value.startsWith("~/")) {
        value.replaceFirst("~", userHome())
      } else {
        value
      }
    }()

    private fun userHome() = System.getProperty("user.home")

    private val folderManager = FolderManager("images")

    fun folderPath(): String = folderManager.dir.path
  }
}
