package net.plshark.takeout.exception

open class HttpServerException(val statusCode: Int, message: String? = null, cause: Throwable? = null) :
        RuntimeException(message, cause) {
    constructor(statusCode: Int) : this(statusCode, null, null)
    constructor(statusCode: Int, message: String?) : this(statusCode, message, null)
    constructor(statusCode: Int, cause: Throwable?) : this(statusCode, null, cause)
}
