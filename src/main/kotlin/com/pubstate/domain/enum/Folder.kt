package com.pubstate.domain.enum

enum class Folder {
  AVATAR, IMAGE;

  val folderName: String = name.toLowerCase()
}