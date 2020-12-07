package com.pubstate.tool

import com.fasterxml.jackson.databind.ObjectMapper
import com.pubstate.annotation.KotlinNoArg
import com.pubstate.domain.entity.Article
import com.pubstate.domain.entity.User
import com.pubstate.domain.enum.FormatType
import com.pubstate.domain.service.ArticleService
import com.pubstate.util.UniqueIdUtil
import java.sql.Timestamp
import java.text.SimpleDateFormat

/**
 * Converts an article to JSON
 */
class ArticleTool {

  fun importBatch(articleArrayJson: String, articleService: ArticleService): Int {
    val objectMapper = ObjectMapper()
    val type = objectMapper.typeFactory.constructCollectionType(List::class.java, ArticleInfo::class.java)
    return (objectMapper.readValue(articleArrayJson, type) as List<ArticleInfo>).map {
      it.id = UniqueIdUtil.newId()
      it.authorId = UniqueIdUtil.one()
      it.save(articleService, null)
    }.count()
  }

  fun import(articleJson: String, articleService: ArticleService, assignedId: String? = null) {
    ObjectMapper().readValue(articleJson, ArticleInfo::class.java).save(articleService, assignedId)
  }

  fun export(article: Article): String = article.run {
    ArticleInfo(
        id = id,
        version = version,
        whenCreated = formatTimestamp(whenCreated),
        whenModified = formatTimestamp(whenModified),
        title = title,
        inputContent = inputContent,
        formatType = formatType,
        authorId = author.id,
        deleted = deleted)
  }.let {
    ObjectMapper().writeValueAsString(it)
  }

  @KotlinNoArg
  data class ArticleInfo(
      var id: String?,
      var version: Long?,
      var whenCreated: String?,
      var whenModified: String?,
      var title: String,
      var inputContent: String,
      var outputContent: String = "",
      var formatType: FormatType,
      var authorId: String,
      var deleted: Boolean?
  ) {
  }

  private fun ArticleInfo.save(articleService: ArticleService, assignedId: String?) {
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
    whenCreated?.let { ar.whenCreated = parseTimestamp(it) }
    whenModified?.let { ar.whenModified = parseTimestamp(it) }
    deleted?.let { ar.deleted = it }

    ar.save()
  }

  private fun parseTimestamp(value: String): Timestamp {
    return Timestamp(SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(value).time)
  }

  private fun formatTimestamp(timestamp: Timestamp): String {
    return SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(timestamp)
  }
}
