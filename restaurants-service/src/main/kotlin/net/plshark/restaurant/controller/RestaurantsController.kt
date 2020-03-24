package net.plshark.restaurant.controller

import net.plshark.restaurant.exception.NotFoundException
import net.plshark.restaurant.model.Restaurant
import net.plshark.restaurant.repository.RestaurantsRepository
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.OffsetDateTime

@RestController
@RequestMapping("/restaurants")
class RestaurantsController(private val repository: RestaurantsRepository) {

    @PostMapping
    fun create(@RequestBody restaurant: Restaurant): Mono<Restaurant> {
        return repository.insert(restaurant.copy(id = null, createTime = OffsetDateTime.now()))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable("id") id: Long): Mono<Restaurant> {
        return repository.findById(id)
                .switchIfEmpty(Mono.error { NotFoundException("No restaurant found for ID $id") })
    }

    // TODO redo paged
    @GetMapping
    fun findAll(@RequestParam(name = "limit", defaultValue = "50") limit: Int,
                @RequestParam(name = "page", defaultValue = "0") page: Int): Flux<Restaurant> {
        return repository.findAll(limit, page)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable("id") id: Long, @RequestBody restaurant: Restaurant): Mono<Restaurant> {
        val updated = restaurant.copy(id = id)
        return repository.update(updated)
                .filter { i -> i == 0 }
                .flatMap { Mono.error<Any> { NotFoundException("No restaurant found for ID $id") } }
                // TODO this may not return the values actually in the DB for columns that can't be updated
                .then(Mono.just(updated))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: Long): Mono<Void> {
        return repository.delete(id)
                .filter { i -> i == 0 }
                .flatMap { Mono.error<Any> { NotFoundException("No restaurant found for ID $id") } }
                .then()
    }
}
