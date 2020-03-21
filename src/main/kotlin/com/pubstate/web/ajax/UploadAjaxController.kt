package com.pubstate.web.ajax

import com.pubstate.domain.enum.Folder
import com.pubstate.exception.DomainException
import com.pubstate.web.auth.Auth
import com.pubstate.web.base.BaseController
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/upload")
class UploadAjaxController : BaseController() {

  @PostMapping("/image", produces = ["application/json"])
  @ResponseBody
  fun uploadImage(file: MultipartFile, response: HttpServletResponse): Map<String, String> {
    val uid = Auth.checkUid()

    log.info("Uploading image: uid={}, filename={}", uid, file.name)

    try {
      val link = fileService.upload(uid, file, Folder.IMAGE)
      return mapOf("location" to link)
    } catch (e: IOException) {
      throw DomainException("Failed to upload the image!", e)
    }
  }

  companion object {
    private val log = LoggerFactory.getLogger(UploadAjaxController::class.java)
  }
}
