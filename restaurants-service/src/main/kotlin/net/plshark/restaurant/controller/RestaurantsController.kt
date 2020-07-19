package net.plshark.restaurant.controller

import net.plshark.restaurant.RestaurantCreate
import net.plshark.restaurant.exception.NotFoundException
import net.plshark.restaurant.Restaurant
import net.plshark.restaurant.RestaurantsService
import net.plshark.restaurant.repository.RestaurantsRepository
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/restaurants")
class RestaurantsController(private val repository: RestaurantsRepository) : RestaurantsService {

    @PostMapping
    @Transactional
    override fun create(@RequestBody restaurant: RestaurantCreate): Mono<Restaurant> {
        return repository.insert(restaurant)
    }

    @GetMapping("/{id}")
    override fun findById(@PathVariable("id") id: Long): Mono<Restaurant> {
        return repository.findById(id)
            .switchIfEmpty(Mono.error { NotFoundException("No restaurant found for ID $id") })
    }

    // TODO redo pagination
    @GetMapping
    fun findAll(
        @RequestParam(name = "limit", defaultValue = "50") limit: Int,
        @RequestParam(name = "page", defaultValue = "0") page: Int
    ): Flux<Restaurant> {
        return repository.findAll(limit, page)
    }

    @PutMapping("/{id}")
    override fun update(@PathVariable("id") id: Long, @RequestBody restaurant: Restaurant): Mono<Restaurant> {
        val updated = restaurant.copy(id = id)
        return repository.update(updated)
            .filter { i -> i == 0 }
            .flatMap { Mono.error<Any> { NotFoundException("No restaurant found for ID $id") } }
            // TODO this may not return the values actually in the DB for columns that can't be updated
            .then(Mono.just(updated))
    }

    @DeleteMapping("/{id}")
    override fun delete(@PathVariable("id") id: Long): Mono<Void> {
        return repository.delete(id)
            .filter { i -> i == 0 }
            .flatMap { Mono.error<Any> { NotFoundException("No restaurant found for ID $id") } }
            .then()
    }
}
