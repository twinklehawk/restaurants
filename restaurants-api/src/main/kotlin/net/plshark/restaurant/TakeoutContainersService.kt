package net.plshark.restaurant

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface TakeoutContainersService {

    fun create(container: CreateTakeoutContainer): Mono<TakeoutContainer>

    fun findAll(): Flux<TakeoutContainer>

    fun delete(id: Long): Mono<Void>
}
