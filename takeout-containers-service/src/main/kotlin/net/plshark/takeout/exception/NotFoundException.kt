package net.plshark.takeout.exception

class NotFoundException(message: String? = null, cause: Throwable? = null) : HttpServerException(404, message, cause)
