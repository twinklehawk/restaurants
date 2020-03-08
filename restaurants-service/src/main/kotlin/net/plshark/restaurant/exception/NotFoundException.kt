package net.plshark.restaurant.exception

class NotFoundException(message: String? = null, cause: Throwable? = null) : HttpServerException(404, message, cause) {
    constructor() : this(null, null)
    constructor(message: String?) : this(message, null)
    constructor(cause: Throwable?) : this(null, cause)
}
