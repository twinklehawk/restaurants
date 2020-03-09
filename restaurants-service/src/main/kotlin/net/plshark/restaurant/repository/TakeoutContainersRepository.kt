package net.plshark.restaurant.repository

import io.r2dbc.spi.Row
import net.plshark.restaurant.model.TakeoutContainer
import org.springframework.data.domain.Sort
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.data.r2dbc.query.Criteria
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
    fun insert(container: TakeoutContainer): Mono<TakeoutContainer> {
        return client.insert()
                .into(TakeoutContainer::class.java)
                .using(container)
                .map { row: Row -> row.get("id", java.lang.Long::class.java)!!.toLong() }
                .one()
                .switchIfEmpty(Mono.error { IllegalStateException("No ID returned from insert") })
                .map { id -> container.copy(id = id) }
    }

    /**
     * Retrieve all takeout container types
     * @return all takeout containers, can be empty
     */
    fun findAll(): Flux<TakeoutContainer> {
        return client.select()
                .from(TakeoutContainer::class.java)
                .orderBy(Sort.Order.asc("id"))
                .fetch()
                .all()
    }

    /**
     * Delete a container type by ID
     * @param id the ID to delete
     * @return the number of rows deleted, never empty
     */
    fun delete(id: Long): Mono<Int> {
        return client.delete()
                .from(TakeoutContainer::class.java)
                .matching(Criteria.where("id").`is`(id))
                .fetch().rowsUpdated()
    }

    /**
     * Delete all takeout container types
     */
    fun deleteAll(): Mono<Int> {
        return client.delete()
                .from(TakeoutContainer::class.java)
                .fetch().rowsUpdated()
    }
}
