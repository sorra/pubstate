package com.pubstate.tool

import com.fasterxml.jackson.databind.ObjectMapper
import com.pubstate.annotation.KotlinNoArg
import com.pubstate.domain.entity.Article
import com.pubstate.domain.entity.User
import com.pubstate.domain.enum.FormatType
import com.pubstate.domain.service.ArticleService
import java.sql.Timestamp

/**
 * Converts an article to JSON
 */
class ArticleTool {

  fun import(data: String, articleService: ArticleService, assignedId: String? = null) {
    ObjectMapper().readValue(data, ArticleInfo::class.java).run {
      val ar = Article(
          title = title,
          inputContent = inputContent,
          outputContent = articleService.render(inputContent, formatType),
          formatType = formatType,
          author = User.ref(authorId)
      )

      (assignedId ?: id)?.let {
        ar.id = it
      }
      version?.let { ar.version = it }
      whenCreated?.let { ar.whenCreated = it }
      whenModified?.let { ar.whenModified = it }
      ar
    }.run {
      save()
    }
  }

  fun export(article: Article): String = article.run {
    ArticleInfo(
        id = id,
        version = version,
        whenCreated = whenCreated,
        whenModified = whenModified,
        title = title,
        inputContent = inputContent,
        formatType = formatType,
        authorId = author.id)
  }.let {
    ObjectMapper().writeValueAsString(it)
  }

  @KotlinNoArg
  data class ArticleInfo(
      var id: String?,
      var version: Long?,
      var whenCreated: Timestamp?,
      var whenModified: Timestamp?,
      var title: String,
      var inputContent: String,
      var formatType: FormatType,
      var authorId: String
  )
}
