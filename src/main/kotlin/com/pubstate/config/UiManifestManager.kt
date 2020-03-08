package com.pubstate.config

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File
import javax.servlet.ServletContext

/**
 * Resolves UI manifest.json
 */
object UiManifestManager {

  private const val MANIFEST_FILE_PATH: String = "/static/dist/manifest.json"

  private val objectMapper: ObjectMapper = ObjectMapper()

  private lateinit var file: File

  // Refreshable
  private var manifest: Map<String, String> = emptyMap()
  private var lastModified: Long = 0

  fun initialize(servletContext: ServletContext): UiManifestManager {
    file = File(servletContext.getRealPath(MANIFEST_FILE_PATH))
    load()
    return this
  }

  fun getManifest(): Map<String, String> {
    refresh()
    return manifest
  }

  private fun refresh() {
    if (file.lastModified() > lastModified) {
      load()
    }
  }

  private fun load() {
    synchronized(this) {
      val fileLastModified = file.lastModified()
      if (fileLastModified > lastModified) {
        lastModified = fileLastModified
      }

      @Suppress("UNCHECKED_CAST")
      manifest = objectMapper.readValue(file, java.util.Map::class.java) as Map<String, String>
    }
  }
}