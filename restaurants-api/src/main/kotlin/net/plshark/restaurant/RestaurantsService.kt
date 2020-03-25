package net.plshark.restaurant

import reactor.core.publisher.Mono

interface RestaurantsService {

    fun findById(id: Long): Mono<Restaurant>

    fun create(restaurant: Restaurant): Mono<Restaurant>

    fun update(id: Long, restaurant: Restaurant): Mono<Restaurant>

    fun delete(id: Long): Mono<Void>
}
