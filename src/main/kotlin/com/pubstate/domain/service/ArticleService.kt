package com.pubstate.domain.service

import com.pubstate.domain.entity.Article
import com.pubstate.domain.entity.FormatType
import com.pubstate.domain.entity.User
import com.pubstate.domain.permission.ArticlePermission
import com.pubstate.util.JsoupUtil
import com.pubstate.util.MarkdownUtil
import org.springframework.stereotype.Service

@Service
class ArticleService {

  fun create(uid: Long, title: String, content: String, formatType: FormatType): Article {
    return Article(title, content, render(content, formatType), formatType, User.ref(uid)).apply {
      save()
    }
  }

  fun update(uid: Long, id: Long, title: String, content: String, formatType: FormatType): Article {
    val article = mustGet(id)

    ArticlePermission(uid, article).canEdit()

    return article.also {
      it.title = title
      it.inputContent = content
      it.outputContent = render(content, formatType)
      it.formatType = formatType
      it.save()
    }
  }

  fun delete(uid: Long, id: Long) {
    val article = mustGet(id)

    ArticlePermission(uid, article).canDelete()

    article.delete()
  }

  private fun render(inputContent: String, formatType: FormatType): String {
    var outputContent = inputContent
    if (formatType == FormatType.MARKDOWN) {
      outputContent = MarkdownUtil.render(inputContent)
    }
    return JsoupUtil.clean(outputContent)
  }

  fun mustGet(id: Long): Article {
    return Article.mustGet(id)
  }
}