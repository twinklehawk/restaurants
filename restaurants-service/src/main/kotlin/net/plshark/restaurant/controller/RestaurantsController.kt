package net.plshark.restaurant.controller

import net.plshark.restaurant.Restaurant
import net.plshark.restaurant.RestaurantCreate
import net.plshark.restaurant.RestaurantsService
import net.plshark.restaurant.exception.NotFoundException
import net.plshark.restaurant.repository.RestaurantContainersRepository
import net.plshark.restaurant.repository.RestaurantsRepository
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux

@RestController
@RequestMapping("/restaurants")
class RestaurantsController(
    private val repository: RestaurantsRepository,
    private val restaurantContainersRepository: RestaurantContainersRepository
) : RestaurantsService {

    @PostMapping
    @Transactional
    override fun create(@RequestBody create: RestaurantCreate): Mono<Restaurant> {
        return repository.insert(create)
            .flatMap { restaurant ->
                create.containerIds.toFlux()
                    .flatMap { restaurantContainersRepository.insert(restaurant.id, it) }
                    .then(fillRestaurant(restaurant))
            }
    }

    @GetMapping("/{id}")
    override fun findById(@PathVariable("id") id: Long): Mono<Restaurant> {
        return repository.findById(id)
            .switchIfEmpty(Mono.error { NotFoundException("No restaurant found for ID $id") })
            .flatMap { fillRestaurant(it) }
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
    override fun update(@PathVariable("id") id: Long, @RequestBody update: Restaurant): Mono<Restaurant> {
        val restaurant = update.copy(id = id)
        return repository.update(restaurant)
            .filter { i -> i == 0L }
            .flatMap { Mono.error<Any> { NotFoundException("No restaurant found for ID $id") } }
            .thenMany(restaurantContainersRepository.getContainersForRestaurant(restaurant.id))
            .collectList()
            .flatMapMany { existing ->
                val updateIds = update.containers.map { it.id }
                val existingIds = existing.map { it.id }
                val toDelete = existingIds.filter { !updateIds.contains(it) }
                val toAdd = updateIds.filter { id: Long -> !existingIds.contains(id) }

                toDelete.toFlux()
                    .flatMap { restaurantContainersRepository.delete(it) }
                    .thenMany(toAdd.toFlux())
                    .flatMap { restaurantContainersRepository.insert(restaurant.id, it) }
            }.then(fillRestaurant(restaurant))
    }

    @DeleteMapping("/{id}")
    override fun delete(@PathVariable("id") id: Long): Mono<Void> {
        return repository.delete(id)
            .filter { i -> i == 0L }
            .flatMap { Mono.error<Any> { NotFoundException("No restaurant found for ID $id") } }
            .then()
    }

    /**
     * Fill in a restaurant's takeout container types
     * @param restaurant the restaurant to fill
     * @return a [Mono] containing the filled in restaurant
     */
    private fun fillRestaurant(restaurant: Restaurant): Mono<Restaurant> {
        return restaurantContainersRepository.getContainersForRestaurant(restaurant.id)
            .collectList()
            .map { restaurant.copy(containers = it) }
    }
}
