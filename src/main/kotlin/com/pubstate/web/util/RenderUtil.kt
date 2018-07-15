package com.pubstate.web.util

import com.pubstate.util.Settings

object RenderUtil {
  private val CDN: String = Settings.props.getProperty("cdn") ?: ""

  private val ENV_MODE: String = Settings.props.getProperty("envMode") ?: "dev"

  @JvmStatic fun cdn() = CDN

  @JvmStatic fun resource(path: String) = CDN + path

  @JvmStatic fun envMode() = ENV_MODE

  @JvmStatic fun paginationLinks(uri: String, pagesCount: Int, curpageNum: Int): String {
    val sb = StringBuilder()
    if (curpageNum > 1) {
      sb.append("<a href=\"${uri}?pageNum=${curpageNum -1}\">上一页</a>")
    }
    for (i in 1..pagesCount) {
      val attrs =
          if (i == curpageNum) "class=\"page-link current-page-link\""
          else "class=\"page-link\" href=\"${uri}?pageNum=${i}\""
      sb.append('\n').append("<a ${attrs}>${i}</a>")
    }
    if (curpageNum < pagesCount) {
      sb.append("\n<a href=\"${uri}?pageNum=${curpageNum +1}\">下一页</a>")
    }
    return sb.toString()
  }
}