package net.plshark.restaurant.controller

import net.plshark.restaurant.exception.NotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * Handler for custom exception mapping
 */
@RestControllerAdvice
class ExceptionHandlerAdvice {

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(e: NotFoundException, request: ServerHttpRequest): ResponseEntity<ErrorResponse> {
        val status = HttpStatus.NOT_FOUND
        return ResponseEntity
            .status(status)
            .body(buildResponse(status, e, request))
    }

    private fun buildResponse(status: HttpStatus, e: Throwable, request: ServerHttpRequest): ErrorResponse {
        return ErrorResponse(
            status = status.value(),
            statusDetail = status.reasonPhrase,
            message = e.message,
            path = request.uri.toString()
        )
    }
}
