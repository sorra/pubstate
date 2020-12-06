package com.pubstate.domain.i18n

import com.pubstate.util.Settings
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import java.util.*

@Component
class MessageBundle(
    @Autowired messageSource: MessageSource) {
  init {
    initialize(messageSource)
  }

  /**
   * Initialized after Spring
   */
  companion object {
    private val locale: Locale = Settings.getProperty("site.locale")!!.let {
      val localeTokens = it.split("_")
      Locale(localeTokens[0], localeTokens[1])
    }

    private lateinit var messageSource: MessageSource

    private fun initialize(messageSource: MessageSource) {
      this.messageSource = messageSource
    }

    /**
     * Initialized after Spring, do NOT use in static or singleton
     */
    fun getMessage(code: String, args: Array<Any> = emptyArray()): String {
      return messageSource.getMessage(code, args, locale)
    }
  }
}
