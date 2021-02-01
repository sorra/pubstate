package com.pubstate.web

import com.pubstate.domain.i18n.MessageBundle
import com.pubstate.domain.service.HasServices
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Scope(WebApplicationContext.SCOPE_REQUEST)
abstract class BaseController : HasServices() {
  @Autowired
  protected lateinit var request: HttpServletRequest
  @Autowired
  protected lateinit var response: HttpServletResponse

  fun param(name: String): String? = request.getParameter(name)

  fun param(name: String, defaultValue: String): String = param(name) ?: defaultValue

  fun fetchParam(name: String): String = param(name) ?: throw MissingServletRequestParameterException(name, "")

  fun paramArray(name: String): Array<String> = request.getParameterValues(name) ?: emptyArray()

  fun paramArray(name: String, defaultValue: Array<String>): Array<String> = paramArray(name).let {
    if (it.isEmpty()) defaultValue
    else it
  }

  fun fetchParamArray(name: String): Array<String> =
      request.getParameterValues(name) ?: throw MissingServletRequestParameterException(name, "Array")

  fun tagIds() = paramArray("tagIds[]").map(String::toLong).toSet()

  fun pageNum() = param("pageNum")?.toInt() ?: 1

  fun pageSize() = param("pageSize")?.toInt() ?: 20

  fun pagedModelAndView(listName: String, list: List<*>, pagesCount: Int, pageNum: Int): ModelAndView =
      pagedModelAndView(listName, listName, list, pagesCount, pageNum)

  fun pagedModelAndView(viewName: String, listName: String, list: List<*>, pagesCount: Int, pageNum: Int): ModelAndView =
      ModelAndView(viewName)
          .addObject(listName, list)
          .addObject("paginationLinks", paginationLinks("/$listName", pagesCount, pageNum))

  private fun paginationLinks(uri: String, pagesCount: Int, curpageNum: Int): String {
    if (pagesCount <= 1) {
      return ""
    }

    val sb = StringBuilder()
    if (curpageNum > 1) {
      sb.append("<a href=\"${uri}?pageNum=${curpageNum -1}\">${MessageBundle.getMessage("ui_previous_page")}</a>")
    }
    for (i in 1..pagesCount) {
      sb.append('\n')
      if (i == curpageNum) {
        sb.append("<span>${i}</span>")
      } else {
        sb.append("<a href=\"${uri}?pageNum=${i}\">${i}</a>")
      }
    }
    if (curpageNum < pagesCount) {
      sb.append("\n<a href=\"${uri}?pageNum=${curpageNum +1}\">${MessageBundle.getMessage("ui_next_page")}</a>")
    }
    return sb.toString()
  }
}
