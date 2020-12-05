package com.pubstate.domain.service

import com.pubstate.domain.entity.FileItem
import com.pubstate.domain.enum.Folder
import com.pubstate.domain.permission.FileItemPermission
import com.pubstate.util.Settings
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.*

@Service
class FileService {

  /**
   * @return file name
   */
  @Throws(IOException::class)
  fun upload(userId: String, file: MultipartFile, folder: Folder): String {
    val fm = findFolderManager(folder)
    return saveFile(userId, fm, folder, file)
  }

  @Throws(IOException::class)
  fun multiUpload(userId: String, files: Array<MultipartFile>, folder: Folder): Collection<String> {
    val fm = findFolderManager(folder)
    return files.map { file ->
      saveFile(userId, fm, folder, file)
    }
  }

  fun delete(userId: String, fileId: String) {
    val fileItem = FileItem.byId(fileId)
        ?: return

    FileItemPermission(userId, fileItem).canDelete()
    fileItem.delete()
  }

  /**
   * @return file name
   */
  @Throws(IOException::class)
  private fun saveFile(ownerId: String, fm: FolderManager, folder: Folder, file: MultipartFile): String {
    if (file.size == 0L) {
      throw IllegalArgumentException("Rejected: file is empty!")
    }
    if (file.size > MAX_BYTES) {
      throw IllegalArgumentException("Rejected: allowed max file size is ${MAX_BYTES}MB!")
    }

    // Restrict file suffix, in order to prevent XSS attack
    val fileName = UUID.randomUUID().toString() + SUFFIX
    val filePath = Paths.get(fm.dir.path, fileName)

    Files.write(filePath, file.bytes, StandardOpenOption.CREATE_NEW)
    FileItem(fileName, folder.name, ownerId, filePath.toString()).save()
    log.info("File saved: {}", filePath)

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
    // Only for trial
    private const val SUFFIX = ".jpg"

    private val log = LoggerFactory.getLogger(FileService::class.java)

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

    private val folderManagers: Map<Folder, FolderManager> = {
      val map: MutableMap<Folder, FolderManager> = EnumMap(Folder::class.java)
      for (key in Folder.values()) {
        map[key] = FolderManager(key.folderName)
      }
      map
    }()

    private fun findFolderManager(folder: Folder): FolderManager {
      return folderManagers[folder] ?: error("No manager for the folder: " + folder)
    }

    fun folderFilePath(folder: Folder): String = findFolderManager(folder).dir.path
  }
}
