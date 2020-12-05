package com.pubstate.domain.entity

import com.pubstate.util.UniqueIdUtil
import io.ebean.Model
import io.ebean.annotation.WhenCreated
import io.ebean.annotation.WhenModified
import java.sql.Timestamp
import javax.persistence.Column
import javax.persistence.Id
import javax.persistence.MappedSuperclass
import javax.persistence.Version

/**
 * AutoModel has auto-increment ID & useful stuff
 */
@MappedSuperclass
abstract class AutoModel : Model() {

  @Id
  @Column(columnDefinition = "CHAR(22)")
  var id: String = UniqueIdUtil.newId()

  @Version
  var version: Long = 0

  @WhenCreated
  var whenCreated: Timestamp? = null

  @WhenModified
  var whenModified: Timestamp? = null

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other?.javaClass != javaClass) return false
    other as AutoModel
    if (!UniqueIdUtil.equal(id, other.id)) return false
    return true
  }

  override fun hashCode(): Int {
    return id.hashCode()
  }
}
