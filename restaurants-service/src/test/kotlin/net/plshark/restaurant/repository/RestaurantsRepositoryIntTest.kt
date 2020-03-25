package net.plshark.restaurant.repository

import java.time.OffsetDateTime
import io.r2dbc.spi.ConnectionFactories
import net.plshark.restaurant.Restaurant
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.springframework.data.r2dbc.core.DatabaseClient
import reactor.test.StepVerifier

@EnabledIfSystemProperty(named = "runIntTests", matches = "true")
class RestaurantsRepositoryIntTest {

    private lateinit var repo: RestaurantsRepository

    @BeforeEach
    fun setup() {
        val connectionFactory = ConnectionFactories.get("r2dbc:postgresql://test_user:test_user_pass@localhost:5432/postgres?schema=restaurants")
        repo = RestaurantsRepository(DatabaseClient.create(connectionFactory))
    }

    @AfterEach
    fun cleanup() {
        repo.deleteAll().block()
    }

    @Test
    fun `insert should return the inserted object with the generated ID set`() {
        val restaurant = Restaurant("bears", "paper", OffsetDateTime.now())
        val inserted = repo.insert(restaurant).block()!!

        assertNotNull(inserted.id)
        assertEquals(restaurant.name, inserted.name)
        assertEquals(restaurant.containerType, inserted.containerType)
        assertEquals(restaurant.createTime, inserted.createTime)
    }

    @Test
    fun `findById should return a previously inserted record`() {
        val restaurant = repo.insert(
            Restaurant(
                "bears",
                "paper",
                OffsetDateTime.now()
            )
        ).block()!!

        StepVerifier.create(repo.findById(restaurant.id!!))
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
        val restaurant1 = repo.insert(
            Restaurant(
                "bears",
                "paper",
                OffsetDateTime.now()
            )
        ).block()
        repo.insert(Restaurant("cows", "styrofoam", OffsetDateTime.now())).block()
        val restaurant3 = repo.insert(
            Restaurant(
                "bears",
                "styrofoam",
                OffsetDateTime.now()
            )
        ).block()
        
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
        val restaurant = repo.insert(
            Restaurant(
                "bears",
                "paper",
                OffsetDateTime.now()
            )
        ).block()!!
        val update =
            Restaurant(restaurant.id, "beets", "rocks", restaurant.createTime)

        
        StepVerifier.create(repo.update(update))
                .expectNext(1).verifyComplete()
        StepVerifier.create(repo.findById(restaurant.id!!))
                .expectNext(update).verifyComplete()
    }

    @Test
    fun `update should throw an error if the restaurant does not have an ID`() {
        assertThrows<java.lang.NullPointerException> { repo.update(
            Restaurant(
                "bears",
                "paper"
            )
        ) }
    }

    @Test
    fun `delete should remove a previously inserted record`() {
        val restaurant = repo.insert(
            Restaurant(
                "bears",
                "paper",
                OffsetDateTime.now()
            )
        ).block()!!

        StepVerifier.create(repo.delete(restaurant.id!!))
                .expectNext(1).verifyComplete()
        StepVerifier.create(repo.findById(restaurant.id!!))
                .verifyComplete()
    }

    @Test
    fun `delete should return 0 when no rows are deleted`() {
        StepVerifier.create(repo.delete(8))
                .expectNext(0).verifyComplete()
    }

    @Test
    fun `deleteAll should remove everything in the table`() {
        repo.insert(Restaurant("bears", "paper", OffsetDateTime.now())).block()
        repo.insert(Restaurant("beets", "paper", OffsetDateTime.now())).block()

        StepVerifier.create(repo.deleteAll())
                .expectNext(2).verifyComplete()
        StepVerifier.create(repo.findAll(100, 0))
                .verifyComplete()
    }
}
