package com.pubstate.domain.validator

import com.pubstate.domain.entity.Article
import com.pubstate.exception.BadArgumentException

object ArticleValidator {

  private const val TITLE_MAX_LENGTH = 50
  private const val CONTENT_MAX_LENGTH = 20000
  
  fun validate(article: Article) {
    if (article.title.isBlank()) {
      throw BadArgumentException("Title is blank.")
    }

    if (article.title.length > TITLE_MAX_LENGTH) {
      throw BadArgumentException("Title length ${article.title.length} exceeds max limit ${TITLE_MAX_LENGTH}.")
    }

    if (article.inputContent.isBlank()) {
      throw BadArgumentException("Content is blank!")
    }

    if (article.inputContent.length > CONTENT_MAX_LENGTH) {
      throw BadArgumentException("Content length ${article.inputContent.length} exceeds max limit ${CONTENT_MAX_LENGTH}.")
  }
  }
}