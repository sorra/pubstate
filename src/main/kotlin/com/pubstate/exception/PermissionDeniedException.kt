package com.pubstate.exception

/**
 * Domain permission denied
 */
class PermissionDeniedException : DomainException {
  constructor(message: String) : super(message)

  constructor(format: String, vararg args: Any) : super(format, *args)

  constructor(message: String, cause: Throwable) : super(message, cause)
}
