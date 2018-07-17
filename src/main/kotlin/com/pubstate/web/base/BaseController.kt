package com.pubstate.web.base

import com.pubstate.service.HasServices
import com.pubstate.web.util.RenderUtil
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
      ModelAndView(listName)
          .addObject(listName, list)
          .addObject("paginationLinks", RenderUtil.paginationLinks("/$listName", pagesCount, pageNum))
}
