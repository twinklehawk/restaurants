package net.plshark.restaurant.repository

import io.r2dbc.spi.Row
import net.plshark.restaurant.TakeoutContainer
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.data.relational.core.query.Criteria
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
        return client.insert()
            .into(TABLE)
            .value(RESTAURANT_ID, restaurantId)
            .value(TAKEOUT_CONTAINER_ID, containerId)
            .map { row: Row -> row.get(ID, java.lang.Long::class.java)!!.toLong() }
            .one()
            .switchIfEmpty(Mono.error { IllegalStateException("No ID returned from insert") })
    }

    /**
     * Get all the container types associated with a restaurant
     * @param restaurantId the ID of the restaurant
     * @return a [Flux] containing all the containers
     */
    fun getContainersForRestaurant(restaurantId: Long): Flux<TakeoutContainer> {
        return client.execute("SELECT c.* FROM takeout_containers c, restaurant_containers rc WHERE " +
                "rc.restaurant_id = :restaurantId AND c.id = rc.takeout_container_ID ORDER BY c.id")
            .bind("restaurantId", restaurantId)
            .map { row: Row -> TakeoutContainersRepository.mapRow(row) }
            .all()
    }

    /**
     * Delete a restaurant-container association by ID
     * @param id the association ID
     * @return a [Mono] containing the number of rows deleted
     */
    fun delete(id: Long): Mono<Int> {
        return client.delete()
            .from(TABLE)
            .matching(Criteria.where(ID).`is`(id))
            .fetch().rowsUpdated()
    }

    /**
     * Delete all restaurant-container associations
     * @return a [Mono] containing the number of rows deleted
     */
    fun deleteAll(): Mono<Int> {
        return client.delete()
            .from(TABLE)
            .fetch().rowsUpdated()
    }
}

private const val TABLE = "restaurant_containers"
private const val ID = "id"
private const val RESTAURANT_ID = "restaurant_id"
private const val TAKEOUT_CONTAINER_ID = "takeout_container_id"
