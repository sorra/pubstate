package com.pubstate.domain.entity

import com.pubstate.domain.i18n.MessageBundle
import com.pubstate.exception.DomainException
import com.pubstate.util.PaginationUtil
import io.ebean.Finder
import io.ebean.PagedList
import io.ebean.Query

abstract class BaseFinder<K, T>(private val entityClass: Class<T>) : Finder<K, T>(entityClass) {

  fun mustGet(id: K): T = byId(id) ?: throw DomainException(MessageBundle.getMessage("not_exist",
          arrayOf("${entityClass.simpleName}[$id]")))

  fun findPageDescWithTotalPagesCount(pageNum: Int, pageSize: Int): Pair<List<T>, Int> {
    val page = query().findPageDesc(pageNum, pageSize)
    return page.list to page.totalPageCount
  }

  fun Query<T>.findPageAsc(pageNum: Int, pageSize: Int): PagedList<T> =
      this.order("whenCreated asc")
          .setFirstRow(PaginationUtil.offset(pageNum, pageSize))
          .setMaxRows(pageSize)
          .findPagedList()

  fun Query<T>.findPageDesc(pageNum: Int, pageSize: Int): PagedList<T> =
      this.order("whenCreated desc")
          .setFirstRow(PaginationUtil.offset(pageNum, pageSize))
          .setMaxRows(pageSize)
          .findPagedList()
}
