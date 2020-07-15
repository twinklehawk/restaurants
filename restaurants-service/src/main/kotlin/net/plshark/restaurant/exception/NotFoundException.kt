package net.plshark.restaurant.exception

import org.springframework.http.HttpStatus

class NotFoundException(message: String? = null, cause: Throwable? = null) :
    HttpServerException(HttpStatus.NOT_FOUND.value(), message, cause) {
    constructor() : this(null, null)
    constructor(message: String?) : this(message, null)
    constructor(cause: Throwable?) : this(null, cause)
}
