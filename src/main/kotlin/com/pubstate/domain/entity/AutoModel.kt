package com.pubstate.domain.entity

import com.pubstate.util.IdCommons
import io.ebean.Model
import io.ebean.annotation.WhenCreated
import io.ebean.annotation.WhenModified
import java.sql.Timestamp
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.MappedSuperclass
import javax.persistence.Version

/**
 * AutoModel has auto-increment ID & useful stuff
 */
@MappedSuperclass
abstract class AutoModel : Model() {

  @Id @GeneratedValue
  var id: Long = 0

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
    if (!IdCommons.equal(id, other.id)) return false
    return true
  }

  override fun hashCode(): Int {
    return id.hashCode()
  }
}
