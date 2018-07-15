package com.pubstate.exception

/**
 * Domain logic exception
 */
open class DomainException : RuntimeException {

  constructor(message: String) : super(message)

  constructor(format: String, vararg args: Any) : this(String.format(format, *args))

  constructor(message: String, cause: Throwable) : super(message, cause)

  constructor(cause: Throwable) : super(cause)
}
