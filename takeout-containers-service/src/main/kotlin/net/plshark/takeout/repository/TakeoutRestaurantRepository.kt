package net.plshark.takeout.repository

import io.r2dbc.spi.Row
import net.plshark.takeout.model.TakeoutRestaurant
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.data.r2dbc.query.Criteria
import org.springframework.data.r2dbc.query.Update
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class TakeoutRestaurantRepository(private val client: DatabaseClient) {

    fun insert(restaurant: TakeoutRestaurant): Mono<TakeoutRestaurant> {
        return client.insert()
                .into(TakeoutRestaurant::class.java)
                .using(restaurant)
                .map { row: Row -> row.get("id", Long::class.java) }
                .one()
                .switchIfEmpty(Mono.error { IllegalStateException("No ID returned from insert") })
                .map { id -> restaurant.copy(id = id) }
    }

    fun findAll(limit: Int, page: Int): Flux<TakeoutRestaurant> {
        return client.select()
                .from(TakeoutRestaurant::class.java)
                .page(PageRequest.of(page, limit, Sort.by(Sort.Order.asc("id"))))
                .fetch()
                .all()
    }

    fun findById(id: Long): Mono<TakeoutRestaurant> {
        return client.select()
                .from(TakeoutRestaurant::class.java)
                .matching(Criteria.where("id").`is`(id))
                .fetch()
                .one()
    }

    fun findByName(name: String): Mono<TakeoutRestaurant> {
        return client.select()
                .from(TakeoutRestaurant::class.java)
                .matching(Criteria.where("name").`is`(name))
                .fetch()
                .one()
    }

    fun update(restaurant: TakeoutRestaurant): Mono<Int> {
        if (restaurant.id == null) throw NullPointerException("Restaurant ID cannot be null when updating")
        return client.update()
                .table("takeout_restaurant")
                .using(Update.update("container_type", restaurant.containerType)
                        .set("name", restaurant.name))
                .matching(Criteria.where("id").`is`(restaurant.id))
                .fetch().rowsUpdated()
    }

    fun delete(id: Long): Mono<Int> {
        return client.delete()
                .from(TakeoutRestaurant::class.java)
                .matching(Criteria.where("id").`is`(id))
                .fetch().rowsUpdated()
    }
}
