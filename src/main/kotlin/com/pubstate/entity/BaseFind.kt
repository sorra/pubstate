package com.pubstate.entity

import com.avaje.ebean.ExpressionList
import com.avaje.ebean.Model
import com.avaje.ebean.PagedList
import com.avaje.ebean.util.ClassUtil
import com.pubstate.exception.DomainException

abstract class BaseFind<K, T : Any> : Model.Find<K, T>() {

  @Suppress("UNCHECKED_CAST")
  private val entityType: Class<T> = ClassUtil.getSecondArgumentType(javaClass) as Class<T>

  fun mustGet(id: K): T = Model.db().find(entityType, id)
      ?: throw DomainException("${entityType.simpleName}[$id] does not exist")

  fun findPageDescWithTotalPageCount(pageNum: Int, pageSize: Int): Pair<List<T>, Int> {
    val page = where().findPageDesc(pageNum, pageSize)
    return page.list to page.totalPageCount
  }

  fun ExpressionList<T>.findPageAsc(pageNum: Int, pageSize: Int): PagedList<T> =
      orderBy("id").findPagedList(pageNum - 1, pageSize)

  fun ExpressionList<T>.findPageDesc(pageNum: Int, pageSize: Int): PagedList<T> =
      orderBy("id desc").findPagedList(pageNum - 1, pageSize)
}
