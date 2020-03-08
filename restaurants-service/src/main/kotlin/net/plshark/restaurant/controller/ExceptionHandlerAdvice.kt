package net.plshark.restaurant.controller

import net.plshark.errors.ErrorResponse
import net.plshark.restaurant.exception.HttpServerException
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

    @ExceptionHandler(HttpServerException::class)
    fun handleHttpServerException(e: HttpServerException, request: ServerHttpRequest): ResponseEntity<ErrorResponse> {
        var status = HttpStatus.resolve(e.statusCode)
        if (status == null)
            status = HttpStatus.INTERNAL_SERVER_ERROR
        return ResponseEntity
                .status(status)
                .body(buildResponse(status, e, request))
    }

    private fun buildResponse(status: HttpStatus, e: Throwable, request: ServerHttpRequest): ErrorResponse {
        return ErrorResponse.builder()
                .status(status.value())
                .statusDetail(status.reasonPhrase)
                .message(e.message)
                .path(request.uri.toString())
                .build()
    }
}
