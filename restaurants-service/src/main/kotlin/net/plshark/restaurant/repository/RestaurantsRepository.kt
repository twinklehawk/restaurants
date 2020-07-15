package net.plshark.restaurant.repository

import io.r2dbc.spi.Row
import net.plshark.restaurant.CreateRestaurant
import net.plshark.restaurant.Restaurant
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Update
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.OffsetDateTime

private const val TABLE = "restaurants"
private const val ID = "id"
private const val NAME = "name"
private const val CONTAINER_TYPE = "container_type"
private const val CREATE_TIME = "create_time"

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
    fun insert(restaurant: CreateRestaurant): Mono<Restaurant> {
        return client.insert()
            .into(TABLE)
            .value(NAME, restaurant.name)
            .value(CONTAINER_TYPE, restaurant.containerType)
            .map { row: Row ->
                Pair(
                    row.get(ID, java.lang.Long::class.java)!!.toLong(),
                    row.get(CREATE_TIME, OffsetDateTime::class.java)!!
                )
            }
            .one()
            .switchIfEmpty(Mono.error { IllegalStateException("No ID returned from insert") })
            .map { Restaurant(it.first, restaurant.name, restaurant.containerType, it.second) }
    }

    fun findAll(limit: Int, page: Int): Flux<Restaurant> {
        return client.select()
            .from(TABLE)
            .project("*")
            .page(PageRequest.of(page, limit, Sort.by(Sort.Order.asc(ID))))
            .map(this::mapRow)
            .all()
    }

    /**
     * Find a takeout restaurant by ID
     * @param id the restaurant ID
     * @return the matching restaurant or empty if not found
     */
    fun findById(id: Long): Mono<Restaurant> {
        return client.select()
            .from(TABLE)
            .project("*")
            .matching(Criteria.where(ID).`is`(id))
            .map(this::mapRow)
            .one()
    }

    /**
     * Find takeout restaurants by name
     * @param name the restaurant name
     * @return the matching restaurants, can be empty
     */
    fun findByName(name: String): Flux<Restaurant> {
        return client.select()
            .from(TABLE)
            .project("*")
            .matching(Criteria.where(NAME).`is`(name))
            .map(this::mapRow)
            .all()
    }

    /**
     * Update an existing restaurant by its ID
     *
     * Only the container type and name can be updated
     * @param restaurant the restaurant to update with the new data to set, the ID must not be null
     * @return the number of rows updated, never empty
     */
    fun update(restaurant: Restaurant): Mono<Int> {
        return client.update()
            .table(TABLE)
            .using(Update.update(CONTAINER_TYPE, restaurant.containerType).set(NAME, restaurant.name))
            .matching(Criteria.where(ID).`is`(restaurant.id))
            .fetch().rowsUpdated()
            .flatMap { i ->
                if (i > 1) Mono.error { IllegalStateException("Unexpected number of restaurant rows updated: $i") }
                else Mono.just(i)
            }
    }

    /**
     * Delete a restaurant by ID
     * @param id the ID to delete
     * @return the number of rows deleted, never empty
     */
    fun delete(id: Long): Mono<Int> {
        return client.delete()
            .from(TABLE)
            .matching(Criteria.where(ID).`is`(id))
            .fetch().rowsUpdated()
    }

    /**
     * Delete all restaurants
     */
    fun deleteAll(): Mono<Int> {
        return client.delete()
            .from(TABLE)
            .fetch().rowsUpdated()
    }

    private fun mapRow(r: Row): Restaurant {
        return Restaurant(
            r.get(ID, java.lang.Long::class.java)!!.toLong(),
            r.get(NAME, String::class.java)!!,
            r.get(CONTAINER_TYPE, String::class.java)!!,
            r.get(CREATE_TIME, OffsetDateTime::class.java)!!
        )
    }
}
