package net.plshark.restaurant.repository

import io.r2dbc.spi.Row
import net.plshark.restaurant.model.Restaurant
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.data.r2dbc.query.Criteria
import org.springframework.data.r2dbc.query.Update
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Repository for storing and retrieving restaurants
 */
@Repository
class RestaurantsRepository(private val client: DatabaseClient) {

    /**
     * Insert a new takeout restaurant
     * @param restaurant the data to save
     * @return the saved data, never empty
     */
    fun insert(restaurant: Restaurant): Mono<Restaurant> {
        return client.insert()
                .into(Restaurant::class.java)
                .using(restaurant)
                .map { row: Row -> row.get("id", Long::class.java) }
                .one()
                .switchIfEmpty(Mono.error { IllegalStateException("No ID returned from insert") })
                .map { id -> restaurant.copy(id = id) }
    }

    fun findAll(limit: Int, page: Int): Flux<Restaurant> {
        return client.select()
                .from(Restaurant::class.java)
                .page(PageRequest.of(page, limit, Sort.by(Sort.Order.asc("id"))))
                .fetch()
                .all()
    }

    /**
     * Find a takeout restaurant by ID
     * @param id the restaurant ID
     * @return the matching restaurant or empty if not found
     */
    fun findById(id: Long): Mono<Restaurant> {
        return client.select()
                .from(Restaurant::class.java)
                .matching(Criteria.where("id").`is`(id))
                .fetch()
                .one()
    }

    /**
     * Find takeout restaurants by name
     * @param name the restaurant name
     * @return the matching restaurants, can be empty
     */
    fun findByName(name: String): Flux<Restaurant> {
        return client.select()
                .from(Restaurant::class.java)
                .matching(Criteria.where("name").`is`(name))
                .fetch()
                .all()
    }

    /**
     * Update an existing restaurant by its ID
     * Only the container type and name can be updated
     * @param restaurant the restaurant to update with the new data to set, the ID must not be null
     * @return the number of rows updated, never empty
     */
    fun update(restaurant: Restaurant): Mono<Int> {
        if (restaurant.id == null) throw NullPointerException("Restaurant ID cannot be null when updating")
        return client.update()
                .table("takeout_restaurant")
                .using(Update.update("container_type", restaurant.containerType)
                        .set("name", restaurant.name))
                .matching(Criteria.where("id").`is`(restaurant.id))
                .fetch().rowsUpdated()
                .flatMap { i -> if (i > 1) Mono.error { IllegalStateException("Unexpected number of rows updated: $i") } else Mono.just(i) }
    }

    /**
     * Delete a restaurant by ID
     * @param id the ID to delete
     * @return the number of rows deleted, never empty
     */
    fun delete(id: Long): Mono<Int> {
        return client.delete()
                .from(Restaurant::class.java)
                .matching(Criteria.where("id").`is`(id))
                .fetch().rowsUpdated()
    }
}
