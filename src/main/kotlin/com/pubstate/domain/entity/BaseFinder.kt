package com.pubstate.domain.entity

import com.pubstate.exception.DomainException
import com.pubstate.util.PaginationUtil
import io.ebean.Finder
import io.ebean.PagedList
import io.ebean.Query

abstract class BaseFinder<K, T>(private val entityClass: Class<T>) : Finder<K, T>(entityClass) {

  fun mustGet(id: K): T = byId(id)
      ?: throw DomainException("${entityClass.simpleName}[$id] does not exist")

  fun findPageDescWithTotalPagesCount(pageNum: Int, pageSize: Int): Pair<List<T>, Int> {
    val page = query().findPageDesc(pageNum, pageSize)
    return page.list to page.totalPageCount
  }

  fun Query<T>.findPageAsc(pageNum: Int, pageSize: Int): PagedList<T> =
      this.orderBy("id asc")
          .setFirstRow(PaginationUtil.offset(pageNum, pageSize))
          .setMaxRows(pageSize)
          .findPagedList()

  fun Query<T>.findPageDesc(pageNum: Int, pageSize: Int): PagedList<T> =
      this.orderBy("id desc")
          .setFirstRow(PaginationUtil.offset(pageNum, pageSize))
          .setMaxRows(pageSize)
          .findPagedList()
}
