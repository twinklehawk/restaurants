package net.plshark.restaurant.repository

import io.r2dbc.spi.ConnectionFactories
import net.plshark.restaurant.model.TakeoutContainer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.springframework.data.r2dbc.core.DatabaseClient
import reactor.test.StepVerifier

@EnabledIfSystemProperty(named = "runIntTests", matches = "true")
class TakeoutContainersRepositoryIntTest {

    private lateinit var repo: TakeoutContainersRepository

    @BeforeEach
    fun setup() {
        val connectionFactory = ConnectionFactories.get("r2dbc:postgresql://test_user:test_user_pass@localhost:5432/postgres?schema=restaurants")
        repo = TakeoutContainersRepository(DatabaseClient.create(connectionFactory))
    }

    @AfterEach
    fun cleanup() {
        repo.deleteAll().block()
    }

    @Test
    fun `insert should return the inserted object with the generated ID set`() {
        val container = TakeoutContainer("paper")
        val inserted = repo.insert(container).block()!!

        assertNotNull(inserted.id)
        assertEquals(container.name, inserted.name)
    }

    @Test
    fun `delete should remove a previously inserted record`() {
        val restaurant = repo.insert(TakeoutContainer("bears")).block()!!

        StepVerifier.create(repo.delete(restaurant.id!!))
                .expectNext(1).verifyComplete()
        StepVerifier.create(repo.findAll())
                .verifyComplete()
    }

    @Test
    fun `delete should return 0 when no rows are deleted`() {
        StepVerifier.create(repo.delete(8))
                .expectNext(0).verifyComplete()
    }

    @Test
    fun `deleteAll should remove everything in the table`() {
        repo.insert(TakeoutContainer("paper")).block()
        repo.insert(TakeoutContainer("paper")).block()

        StepVerifier.create(repo.deleteAll())
                .expectNext(2).verifyComplete()
        StepVerifier.create(repo.findAll())
                .verifyComplete()
    }
}
