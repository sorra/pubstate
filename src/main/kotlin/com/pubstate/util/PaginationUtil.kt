package com.pubstate.util

object PaginationUtil {

  fun offset(pageNum: Int, size: Int) = (pageNum - 1) * size

  fun totalPages(totalRecords: Int, size: Int): Int {
    val division = totalRecords / size
    return if (totalRecords % size == 0) division else division + 1
  }
}