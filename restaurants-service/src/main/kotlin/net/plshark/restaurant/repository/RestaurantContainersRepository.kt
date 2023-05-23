package net.plshark.restaurant.repository

import net.plshark.restaurant.TakeoutContainer
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Repository for storing and retrieving restaurant-takeout container associations
 */
@Repository
class RestaurantContainersRepository(private val client: DatabaseClient) {

    /**
     * Add a container type to a restaurant
     * @param restaurantId the ID of the restaurant
     * @param containerId the ID of the container type
     * @return a [Mono] containing the ID of the association
     */
    fun insert(restaurantId: Long, containerId: Long): Mono<Long> {
        val sql = "INSERT INTO restaurant_containers (restaurant_id, takeout_container_id) VALUES (:restaurantId, " +
            ":takeoutContainerId) RETURNING id"
        return client.sql(sql)
            .bind("restaurantId", restaurantId)
            .bind("takeoutContainerId", containerId)
            .map { row -> row.get("id", java.lang.Long::class.java)!!.toLong() }
            .one()
            .switchIfEmpty(Mono.error { IllegalStateException("No ID returned from insert") })
    }

    /**
     * Get all the container types associated with a restaurant
     * @param restaurantId the ID of the restaurant
     * @return a [Flux] containing all the containers
     */
    fun getContainersForRestaurant(restaurantId: Long): Flux<TakeoutContainer> {
        val sql = "SELECT c.* FROM takeout_containers c, restaurant_containers rc WHERE " +
            "rc.restaurant_id = :restaurantId AND c.id = rc.takeout_container_ID ORDER BY c.id"
        return client.sql(sql)
            .bind("restaurantId", restaurantId)
            .map { row -> TakeoutContainersRepository.mapRow(row) }
            .all()
    }

    /**
     * Delete a restaurant-container association by ID
     * @param id the association ID
     * @return a [Mono] containing the number of rows deleted
     */
    fun delete(id: Long): Mono<Long> {
        return client.sql("DELETE FROM takeout_containers WHERE id = :id")
            .bind("id", id)
            .fetch().rowsUpdated()
    }

    /**
     * Delete all restaurant-container associations
     * @return a [Mono] containing the number of rows deleted
     */
    fun deleteAll(): Mono<Long> {
        return client.sql("DELETE FROM takeout_containers")
            .fetch().rowsUpdated()
    }
}
