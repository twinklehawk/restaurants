package net.plshark.restaurant.repository

import io.r2dbc.spi.Row
import net.plshark.restaurant.TakeoutContainer
import org.springframework.data.domain.Sort
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

    fun insert(restaurantId: Long, containerId: Long): Mono<Long> {
        return client.insert()
            .into(TABLE)
            .value(RESTAURANT_ID, restaurantId)
            .value(TAKEOUT_CONTAINER_ID, containerId)
            .map { row: Row -> row.get(ID, java.lang.Long::class.java)!!.toLong() }
            .one()
            .switchIfEmpty(Mono.error { IllegalStateException("No ID returned from insert") })
    }

    fun getContainersForRestaurant(restaurantId: Long): Flux<TakeoutContainer> {
        return client.select()
            .from(TABLE)
            .project("*")
            .matching(Criteria.where(RESTAURANT_ID).`is`(restaurantId))
            .orderBy(Sort.Order.asc(ID))
            .map { row: Row -> TakeoutContainersRepository.mapRow(row) }
            .all()
    }

    fun delete(id: Long): Mono<Int> {
        return client.delete()
            .from(TABLE)
            .matching(Criteria.where(ID).`is`(id))
            .fetch().rowsUpdated()
    }

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
