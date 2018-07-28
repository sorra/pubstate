package com.pubstate.service

import org.springframework.beans.factory.annotation.Autowired

abstract class HasServices {

  @Autowired
  protected lateinit var userService: UserService

  @Autowired
  protected lateinit var userAuthService: UserAuthService

  @Autowired
  protected lateinit var articleService: ArticleService

  @Autowired
  protected lateinit var commentService: CommentService
}
