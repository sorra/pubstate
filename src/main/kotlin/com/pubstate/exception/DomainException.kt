package com.pubstate.exception

/**
 * Domain logic exception
 * Should accept an i18n message
 */
open class DomainException : RuntimeException {

  constructor(message: String) : super(message)

  constructor(message: String, cause: Throwable) : super(message, cause)

  constructor(cause: Throwable) : super(cause)
}
