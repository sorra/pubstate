package com.pubstate.service

import com.pubstate.entity.Article
import com.pubstate.entity.FormatType
import com.pubstate.entity.User
import com.pubstate.permission.ArticlePermission
import org.springframework.stereotype.Service

@Service
class ArticleService {
  fun create(uid: Long, title: String, content: String, formatType: FormatType): Article {
    return Article(title, content, parse(content, formatType), formatType, User.ref(uid)).apply {
      save()
    }
  }

  fun update(uid: Long, id: Long, title: String, content: String, formatType: FormatType): Article {
    val article = Article.mustGet(id)

    ArticlePermission(uid, article).canEdit()

    return article.also {
      it.title = title
      it.inputContent = content
      it.outputContent = parse(content, formatType)
      it.formatType = formatType
      it.save()
    }
  }

  fun delete(uid: Long, id: Long) {
    val article = Article.mustGet(id)

    ArticlePermission(uid, article).canDelete()

    article.delete()
  }

  private fun parse(inputContent: String, formatType: FormatType): String {
    throw UnsupportedOperationException()
  }

  fun mustGet(id: Long): Article {
    return Article.mustGet(id)
  }
}