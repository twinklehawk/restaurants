package net.plshark.restaurant.repository

import io.r2dbc.spi.Row
import net.plshark.restaurant.TakeoutContainer
import net.plshark.restaurant.TakeoutContainerCreate
import org.springframework.data.domain.Sort
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.data.relational.core.query.Criteria
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

private const val TABLE = "takeout_containers"
private const val ID = "id"
private const val NAME = "name"

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
        return client.insert()
            .into(TABLE)
            .value(NAME, container.name)
            .map { row: Row -> row.get(ID, java.lang.Long::class.java)!!.toLong() }
            .one()
            .switchIfEmpty(Mono.error { IllegalStateException("No ID returned from insert") })
            .map { container.toTakeoutContainer(it) }
    }

    /**
     * Retrieve all takeout container types
     * @return all takeout containers, can be empty
     */
    fun findAll(): Flux<TakeoutContainer> {
        return client.select()
            .from(TABLE)
            .project("*")
            .orderBy(Sort.Order.asc(ID))
            .map { row: Row -> mapRow(row) }
            .all()
    }

    /**
     * Delete a container type by ID
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
     * Delete all takeout container types
     */
    fun deleteAll(): Mono<Int> {
        return client.delete()
            .from(TABLE)
            .fetch().rowsUpdated()
    }

    companion object {
        /**
         * Map a [Row] to a [TakeoutContainer]
         */
        fun mapRow(r: Row): TakeoutContainer {
            return TakeoutContainer(
                r.get(ID, java.lang.Long::class.java)!!.toLong(),
                r.get(NAME, String::class.java)!!
            )
        }
    }
}
