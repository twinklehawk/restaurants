package net.plshark.restaurant

import net.plshark.restaurant.exception.NotFoundException
import reactor.core.publisher.Mono

interface RestaurantsService {

    /**
     * Find a restaurant by its ID
     * @param id the ID
     * @return a [Mono] containing the restaurant or a [NotFoundException] if not found
     */
    fun findById(id: Long): Mono<Restaurant>

    /**
     * Create a restaurant
     * @param create the data for the new restaurant
     * @return a [Mono] containing the created restaurant
     */
    fun create(create: RestaurantCreate): Mono<Restaurant>

    /**
     * Update an existing restaurant
     * @param id the ID of the restaurant to update
     * @param update the data to update, the ID will be ignored
     * @return a [Mono] containing the updated restaurant or a [NotFoundException] if not found
     */
    fun update(id: Long, update: Restaurant): Mono<Restaurant>

    /**
     * Delete an existing restaurant
     * @param id the ID of the restaurant to delete
     * @return a [Mono] indicating when complete or containing a [NotFoundException] if not found
     */
    fun delete(id: Long): Mono<Void>
}
