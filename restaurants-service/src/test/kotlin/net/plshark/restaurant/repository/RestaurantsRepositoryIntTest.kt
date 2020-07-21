package net.plshark.restaurant.repository

import net.plshark.restaurant.Restaurant
import net.plshark.restaurant.RestaurantCreate
import net.plshark.restaurant.test.DbIntTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.kotlin.test.test

class RestaurantsRepositoryIntTest : DbIntTest() {

    private lateinit var repo: RestaurantsRepository

    @BeforeEach
    fun setup() {
        repo = RestaurantsRepository(databaseClient)
    }

    @AfterEach
    fun cleanup() {
        repo.deleteAll().block()
    }

    @Test
    fun `insert should return the inserted object with the generated ID set`() {
        val restaurant = RestaurantCreate("bears", "burgers", null, emptyList())
        val inserted = repo.insert(restaurant).block()!!

        assertEquals(restaurant.toRestaurant(inserted.id), inserted.name)
    }

    @Test
    fun `findById should return a previously inserted record`() {
        val restaurant = repo.insert(RestaurantCreate("bears", "bad", "1234 street", emptyList())).block()!!

        repo.findById(restaurant.id).test()
            .expectNext(restaurant)
            .verifyComplete()
    }

    @Test
    fun `findById should return empty when no record matches`() {
        repo.findById(18).test()
            .verifyComplete()
    }

    @Test
    fun `findByName should return the matching records`() {
        val restaurant1 = repo.insert(RestaurantCreate("bears", "burgers", null, emptyList())).block()!!
        repo.insert(RestaurantCreate("cows", "burgers", null, emptyList())).block()
        val restaurant3 = repo.insert(RestaurantCreate("bears", "burgers", null, emptyList())).block()!!

        repo.findByName("bears").test()
            .expectNext(restaurant1)
            .expectNext(restaurant3)
            .verifyComplete()
    }

    @Test
    fun `findByName should return empty when there are no matches`() {
        repo.findByName("reindeer").test()
            .verifyComplete()
    }

    @Test
    fun `update should set the name, type, and address`() {
        var restaurant = repo.insert(RestaurantCreate("bears", "burgers", null, emptyList())).block()!!
        restaurant = repo.findById(restaurant.id).block()!!
        val update = Restaurant(restaurant.id, "beets", "rocks", "address", emptyList())

        repo.update(update).test()
            .expectNext(1).verifyComplete()
        repo.findById(restaurant.id).test()
            .expectNext(update).verifyComplete()
    }

    @Test
    fun `delete should remove a previously inserted record`() {
        val restaurant = repo.insert(RestaurantCreate("bears", "burgers", null, emptyList())).block()!!

        repo.delete(restaurant.id).test()
            .expectNext(1).verifyComplete()
        repo.findById(restaurant.id).test()
            .verifyComplete()
    }

    @Test
    fun `delete should return 0 when no rows are deleted`() {
        repo.delete(8).test()
            .expectNext(0).verifyComplete()
    }

    @Test
    fun `deleteAll should remove everything in the table`() {
        repo.insert(RestaurantCreate("bears", "burgers", null, emptyList())).block()
        repo.insert(RestaurantCreate("beets", "burgers", null, emptyList())).block()

        repo.deleteAll().test()
            .expectNext(2).verifyComplete()
        repo.findAll(100, 0).test()
            .verifyComplete()
    }
}
