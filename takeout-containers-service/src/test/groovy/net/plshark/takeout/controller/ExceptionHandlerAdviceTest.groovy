package net.plshark.takeout.controller

import net.plshark.takeout.exception.HttpServerException
import net.plshark.takeout.exception.NotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import spock.lang.Specification

class ExceptionHandlerAdviceTest extends Specification {

    def handler = new ExceptionHandlerAdvice()
    def request = Mock(ServerHttpRequest)

    def 'should build a response with the status code and message from the HttpServerException and the URL from the request'() {
        request.getURI() >> URI.create('http://localhost/test/path')

        when:
        def response = handler.handleHttpServerException(new NotFoundException('test missing'), request)

        then:
        response.statusCode == HttpStatus.NOT_FOUND
        response.body.status == 404
        response.body.statusDetail == HttpStatus.NOT_FOUND.reasonPhrase
        response.body.message == 'test missing'
        response.body.path == 'http://localhost/test/path'
        response.body.timestamp != null
    }

    def 'should map to an internal server error code when the status is not recognized'() {
        request.getURI() >> URI.create('http://localhost/test/path')

        when:
        def response = handler.handleHttpServerException(new HttpServerException(600, 'made up'), request)

        then:
        response.statusCode == HttpStatus.INTERNAL_SERVER_ERROR
        response.body.status == 500
        response.body.statusDetail == HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase
    }
}
