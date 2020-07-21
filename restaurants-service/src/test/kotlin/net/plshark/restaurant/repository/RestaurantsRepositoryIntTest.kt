package net.plshark.restaurant.repository

import net.plshark.restaurant.Restaurant
import net.plshark.restaurant.RestaurantCreate
import net.plshark.restaurant.test.DbIntTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier

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

        StepVerifier.create(repo.findById(restaurant.id))
            .expectNext(restaurant)
            .verifyComplete()
    }

    @Test
    fun `findById should return empty when no record matches`() {
        StepVerifier.create(repo.findById(18))
            .verifyComplete()
    }

    @Test
    fun `findByName should return the matching records`() {
        val restaurant1 = repo.insert(RestaurantCreate("bears", "burgers", null, emptyList())).block()!!
        repo.insert(RestaurantCreate("cows", "burgers", null, emptyList())).block()
        val restaurant3 = repo.insert(RestaurantCreate("bears", "burgers", null, emptyList())).block()!!

        StepVerifier.create(repo.findByName("bears"))
            .expectNext(restaurant1)
            .expectNext(restaurant3)
            .verifyComplete()
    }

    @Test
    fun `findByName should return empty when there are no matches`() {
        StepVerifier.create(repo.findByName("reindeer"))
            .verifyComplete()
    }

    @Test
    fun `update should set the name, type, and address`() {
        var restaurant = repo.insert(RestaurantCreate("bears", "burgers", null, emptyList())).block()!!
        restaurant = repo.findById(restaurant.id).block()!!
        val update = Restaurant(restaurant.id, "beets", "rocks", "address", emptyList())

        StepVerifier.create(repo.update(update))
            .expectNext(1).verifyComplete()
        StepVerifier.create(repo.findById(restaurant.id))
            .expectNext(update).verifyComplete()
    }

    @Test
    fun `delete should remove a previously inserted record`() {
        val restaurant = repo.insert(RestaurantCreate("bears", "burgers", null, emptyList())).block()!!

        StepVerifier.create(repo.delete(restaurant.id))
            .expectNext(1).verifyComplete()
        StepVerifier.create(repo.findById(restaurant.id))
            .verifyComplete()
    }

    @Test
    fun `delete should return 0 when no rows are deleted`() {
        StepVerifier.create(repo.delete(8))
            .expectNext(0).verifyComplete()
    }

    @Test
    fun `deleteAll should remove everything in the table`() {
        repo.insert(RestaurantCreate("bears", "burgers", null, emptyList())).block()
        repo.insert(RestaurantCreate("beets", "burgers", null, emptyList())).block()

        StepVerifier.create(repo.deleteAll())
            .expectNext(2).verifyComplete()
        StepVerifier.create(repo.findAll(100, 0))
            .verifyComplete()
    }
}
