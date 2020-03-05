package net.plshark.takeout.exception

open class HttpServerException(val statusCode: Int, message: String? = null, cause: Throwable? = null) :
        RuntimeException(message, cause)
