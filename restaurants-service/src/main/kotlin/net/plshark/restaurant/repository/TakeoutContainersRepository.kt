package net.plshark.restaurant.repository

import io.r2dbc.spi.Readable
import io.r2dbc.spi.Row
import net.plshark.restaurant.TakeoutContainer
import net.plshark.restaurant.TakeoutContainerCreate
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Repository for storing and retrieving takeout container types
 */
@Repository
class TakeoutContainersRepository(private val client: DatabaseClient) {

    /**
     * Insert a new takeout container type
     * @param container the data to save
     * @return the saved data, never empty
     */
    fun insert(container: TakeoutContainerCreate): Mono<TakeoutContainer> {
        return client.sql("INSERT INTO takeout_containers (name) VALUES (:name) RETURNING id")
            .bind(NAME, container.name)
            .map { row -> row.get(ID, java.lang.Long::class.java)!!.toLong() }
            .one()
            .switchIfEmpty(Mono.error { IllegalStateException("No ID returned from insert") })
            .map { container.toTakeoutContainer(it) }
    }

    /**
     * Retrieve all takeout container types
     * @return all takeout containers, can be empty
     */
    fun findAll(): Flux<TakeoutContainer> {
        return client.sql("SELECT * FROM takeout_containers ORDER BY id")
            .map { row -> mapRow(row) }
            .all()
    }

    /**
     * Delete a container type by ID
     * @param id the ID to delete
     * @return the number of rows deleted, never empty
     */
    fun delete(id: Long): Mono<Long> {
        return client.sql("DELETE FROM takeout_containers WHERE id = :id")
            .bind("id", id)
            .fetch().rowsUpdated()
    }

    /**
     * Delete all takeout container types
     */
    fun deleteAll(): Mono<Long> {
        return client.sql("DELETE FROM takeout_containers")
            .fetch().rowsUpdated()
    }

    companion object {

        const val ID = "id"
        const val NAME = "name"

        /**
         * Map a [Row] to a [TakeoutContainer]
         */
        fun mapRow(r: Readable): TakeoutContainer {
            return TakeoutContainer(
                r.get(ID, java.lang.Long::class.java)!!.toLong(),
                r.get(NAME, String::class.java)!!,
            )
        }
    }
}
