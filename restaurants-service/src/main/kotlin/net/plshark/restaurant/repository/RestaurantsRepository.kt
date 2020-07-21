package net.plshark.restaurant.repository

import io.r2dbc.spi.Row
import net.plshark.restaurant.Restaurant
import net.plshark.restaurant.RestaurantCreate
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.data.r2dbc.mapping.SettableValue
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Update
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
    fun insert(restaurant: RestaurantCreate): Mono<Restaurant> {
        return client.insert()
            .into(TABLE)
            .value(NAME, restaurant.name)
            .value(TYPE, restaurant.type)
            .value(ADDRESS, SettableValue.fromOrEmpty(restaurant.address, String::class.java))
            .map { row: Row -> row.get(ID, java.lang.Long::class.java)!!.toLong() }
            .one()
            .switchIfEmpty(Mono.error { IllegalStateException("No ID returned from insert") })
            .map { restaurant.toRestaurant(it) }
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
     * @param restaurant the restaurant to update with the new data to set
     * @return the number of rows updated, never empty
     */
    fun update(restaurant: Restaurant): Mono<Int> {
        return client.update()
            .table(TABLE)
            .using(Update.update(NAME, restaurant.name).set(TYPE, restaurant.type).set(ADDRESS, restaurant.address))
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
            r.get(TYPE, String::class.java)!!,
            r.get(ADDRESS, String::class.java),
            emptyList()
        )
    }
}

private const val TABLE = "restaurants"
private const val ID = "id"
private const val NAME = "name"
private const val TYPE = "type"
private const val ADDRESS = "address"
