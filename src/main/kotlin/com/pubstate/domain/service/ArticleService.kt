package com.pubstate.domain.service

import com.pubstate.domain.entity.Article
import com.pubstate.domain.entity.FormatType
import com.pubstate.domain.entity.User
import com.pubstate.domain.permission.ArticlePermission
import com.pubstate.util.HtmlUtil
import com.pubstate.util.MarkdownUtil
import org.springframework.stereotype.Service

@Service
class ArticleService {

  fun create(uid: Long, title: String, content: String, formatType: FormatType): Article {
    return Article(
        title = title,
        inputContent = content,
        outputContent = render(content, formatType),
        formatType = formatType,
        author = User.ref(uid)
    ).also {
      it.save()
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

  private fun render(input: String, formatType: FormatType): String {
    var output = input
    if (formatType == FormatType.MARKDOWN) {
      output = MarkdownUtil.render(input)
    }
    // process HTML anyway
    return HtmlUtil.secureClean(output)
  }

  fun mustGet(id: Long): Article {
    return Article.mustGet(id)
  }
}