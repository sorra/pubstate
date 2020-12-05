package com.pubstate.domain.entity

import javax.persistence.Entity

@Entity
class FileItem(
    var name: String,
    var kind: String,
    var ownerId: String,
    /** just for information */
    var storedPath: String
) : AutoModel() {

  companion object : BaseFinder<String, FileItem>(FileItem::class.java)
}