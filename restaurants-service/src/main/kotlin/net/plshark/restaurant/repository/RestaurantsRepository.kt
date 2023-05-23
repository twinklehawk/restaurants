package net.plshark.restaurant.repository

import io.r2dbc.spi.Parameters
import io.r2dbc.spi.R2dbcType
import io.r2dbc.spi.Readable
import net.plshark.restaurant.Restaurant
import net.plshark.restaurant.RestaurantCreate
import org.springframework.r2dbc.core.DatabaseClient
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
        val sql = "INSERT INTO restaurants (name, type, address) VALUES (:name, :type, :address) RETURNING id"
        return client.sql(sql)
            .bind(NAME, restaurant.name)
            .bind(TYPE, restaurant.type)
            .bind(ADDRESS, Parameters.`in`(R2dbcType.VARCHAR, restaurant.address))
            .map { row -> row.get(ID, java.lang.Long::class.java)!!.toLong() }
            .one()
            .switchIfEmpty(Mono.error { IllegalStateException("No ID returned from insert") })
            .map { restaurant.toRestaurant(it) }
    }

    fun findAll(limit: Int, page: Int): Flux<Restaurant> {
        val offset = page * limit
        return client.sql("SELECT * FROM restaurants ORDER BY id LIMIT $limit OFFSET $offset")
            .map(this::mapRow)
            .all()
    }

    /**
     * Find a takeout restaurant by ID
     * @param id the restaurant ID
     * @return the matching restaurant or empty if not found
     */
    fun findById(id: Long): Mono<Restaurant> {
        return client.sql("SELECT * FROM restaurants WHERE id = :id")
            .bind("id", id)
            .map(this::mapRow)
            .one()
    }

    /**
     * Find takeout restaurants by name
     * @param name the restaurant name
     * @return the matching restaurants, can be empty
     */
    fun findByName(name: String): Flux<Restaurant> {
        return client.sql("SELECT * FROM restaurants WHERE name = :name")
            .bind("name", name)
            .map(this::mapRow)
            .all()
    }

    /**
     * Update an existing restaurant by its ID
     * @param restaurant the restaurant to update with the new data to set
     * @return the number of rows updated, never empty
     */
    fun update(restaurant: Restaurant): Mono<Long> {
        val sql = "UPDATE restaurants SET name = :name, type = :type, address = :address WHERE id = :id"
        return client.sql(sql)
            .bind("name", restaurant.name)
            .bind("type", restaurant.type)
            .bind("address", Parameters.`in`(R2dbcType.VARCHAR, restaurant.address))
            .bind("id", restaurant.id)
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
    fun delete(id: Long): Mono<Long> {
        return client.sql("DELETE FROM restaurants WHERE id = :id")
            .bind("id", id)
            .fetch().rowsUpdated()
    }

    /**
     * Delete all restaurants
     */
    fun deleteAll(): Mono<Long> {
        return client.sql("DELETE FROM restaurants")
            .fetch().rowsUpdated()
    }

    private fun mapRow(r: Readable): Restaurant {
        return Restaurant(
            r.get(ID, java.lang.Long::class.java)!!.toLong(),
            r.get(NAME, String::class.java)!!,
            r.get(TYPE, String::class.java)!!,
            r.get(ADDRESS, String::class.java),
            emptyList()
        )
    }

    companion object {
        const val ID = "id"
        const val NAME = "name"
        const val TYPE = "type"
        const val ADDRESS = "address"
    }
}
