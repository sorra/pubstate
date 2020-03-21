package com.pubstate.domain.entity

import javax.persistence.Entity

@Entity
class FileItem(
    var name: String,
    var kind: String,
    var ownerId: Long,
    /** just for information */
    var storedPath: String
) : AutoModel() {

  companion object : BaseFinder<Long, FileItem>(FileItem::class.java)
}