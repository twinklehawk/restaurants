package net.plshark.restaurant.controller

import io.mockk.every
import io.mockk.mockk
import net.plshark.restaurant.exception.HttpServerException
import net.plshark.restaurant.exception.NotFoundException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import java.net.URI

class ExceptionHandlerAdviceTest {

    private val handler = ExceptionHandlerAdvice()
    private val request = mockk<ServerHttpRequest>()

    @Test
    fun `should build a response with the status code and message from the HttpServerException and the URL from the request`() {
        every { request.uri } returns URI.create("http://localhost/test/path")

        val response = handler.handleHttpServerException(NotFoundException("test missing"), request)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(404, response.body?.status)
        assertEquals("test missing", response.body?.message)
        assertEquals("http://localhost/test/path", response.body?.path)
        assertNotNull(response.body?.timestamp)
    }

    @Test
    fun `should map to an internal server error code when the status is not recognized`() {
        every { request.uri } returns URI.create("http://localhost/test/path")

        val response = handler.handleHttpServerException(HttpServerException(600, "made up"), request)

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertEquals(500, response.body?.status)
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase, response.body?.statusDetail)
    }
}
