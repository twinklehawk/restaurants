package net.plshark.restaurant.repository

import io.r2dbc.spi.ConnectionFactories
import net.plshark.restaurant.CreateRestaurant
import net.plshark.restaurant.Restaurant
import net.plshark.restaurant.test.IntTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.r2dbc.core.DatabaseClient
import reactor.test.StepVerifier

class RestaurantsRepositoryIntTest : IntTest() {

    private lateinit var repo: RestaurantsRepository

    @BeforeEach
    fun setup() {
        val connectionFactory =
            ConnectionFactories.get("r2dbc:postgresql://test_user:test_user_pass@localhost:5432/postgres?schema=restaurants")
        repo = RestaurantsRepository(DatabaseClient.create(connectionFactory))
    }

    @AfterEach
    fun cleanup() {
        repo.deleteAll().block()
    }

    @Test
    fun `insert should return the inserted object with the generated ID set`() {
        val restaurant = CreateRestaurant("bears", "paper")
        val inserted = repo.insert(restaurant).block()!!

        assertEquals(restaurant.name, inserted.name)
        assertEquals(restaurant.containerType, inserted.containerType)
    }

    @Test
    fun `findById should return a previously inserted record`() {
        val restaurant = repo.insert(CreateRestaurant("bears", "paper")).block()!!

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
        val restaurant1 = repo.insert(CreateRestaurant("bears", "paper")).block()!!
        repo.insert(CreateRestaurant("cows", "styrofoam")).block()
        val restaurant3 = repo.insert(CreateRestaurant("bears", "styrofoam")).block()!!

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
    fun `update should set the name and takeout type`() {
        var restaurant = repo.insert(CreateRestaurant("bears", "paper")).block()!!
        restaurant = repo.findById(restaurant.id).block()!!
        val update = Restaurant(restaurant.id, "beets", "rocks", restaurant.createTime)

        StepVerifier.create(repo.update(update))
            .expectNext(1).verifyComplete()
        StepVerifier.create(repo.findById(restaurant.id))
            .expectNext(update).verifyComplete()
    }

    @Test
    fun `delete should remove a previously inserted record`() {
        val restaurant = repo.insert(CreateRestaurant("bears", "paper")).block()!!

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
        repo.insert(CreateRestaurant("bears", "paper")).block()
        repo.insert(CreateRestaurant("beets", "paper")).block()

        StepVerifier.create(repo.deleteAll())
            .expectNext(2).verifyComplete()
        StepVerifier.create(repo.findAll(100, 0))
            .verifyComplete()
    }
}
