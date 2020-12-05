package com.pubstate.domain.service

import com.pubstate.domain.entity.Article
import com.pubstate.domain.enum.FormatType
import com.pubstate.domain.entity.User
import com.pubstate.domain.permission.ArticlePermission
import com.pubstate.util.HtmlUtil
import com.pubstate.util.MarkdownUtil
import org.springframework.stereotype.Service
import java.sql.Timestamp

@Service
class ArticleService {

  fun create(uid: String, title: String, content: String, formatType: FormatType): Article {
    return Article(
        author = User.ref(uid),
        title = title,
        inputContent = content,
        outputContent = render(content, formatType),
        formatType = formatType
    ).also {
      it.save()
    }
  }

  fun update(uid: String, id: String, title: String, content: String, formatType: FormatType): Article {
    val article = mustGet(id)

    ArticlePermission(uid, article).canEdit()

    return article.also {
      it.title = title
      it.inputContent = content
      it.outputContent = render(content, formatType)
      it.formatType = formatType
      it.whenModified = Timestamp(System.currentTimeMillis())
      it.save()
    }
  }

  fun delete(uid: String, id: String) {
    val article = mustGet(id)

    ArticlePermission(uid, article).canDelete()

    article.delete()
  }

  fun render(input: String, formatType: FormatType): String {
    var output = input
    if (formatType == FormatType.MARKDOWN) {
      output = MarkdownUtil.render(input)
    }
    // process HTML anyway
    return HtmlUtil.secureClean(output)
  }

  fun mustGet(id: String): Article {
    return Article.mustGet(id)
  }

  fun listByAuthor(authorId: String): List<Article> {
    return Article.listByAuthor(authorId)
  }
}