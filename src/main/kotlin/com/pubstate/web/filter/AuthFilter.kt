package com.pubstate.web.filter

import com.pubstate.web.auth.Auth
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AuthFilter : javax.servlet.Filter {

  override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
    if (request is HttpServletRequest && response is HttpServletResponse) {
      request.setAttribute("authPack", Auth.AuthPack(request, response))
    }
    chain.doFilter(request, response)
  }

  override fun init(filterConfig: FilterConfig) {}

  override fun destroy() {}
}