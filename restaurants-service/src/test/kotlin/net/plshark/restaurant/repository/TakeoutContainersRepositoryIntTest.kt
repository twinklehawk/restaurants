package net.plshark.restaurant.repository

import net.plshark.restaurant.TakeoutContainerCreate
import net.plshark.restaurant.test.DbIntTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.kotlin.test.test

class TakeoutContainersRepositoryIntTest : DbIntTest() {

    private lateinit var repo: TakeoutContainersRepository

    @BeforeEach
    fun setup() {
        repo = TakeoutContainersRepository(databaseClient)
    }

    @AfterEach
    fun cleanup() {
        repo.deleteAll().block()
    }

    @Test
    fun `insert should return the inserted object with the generated ID set`() {
        val container = TakeoutContainerCreate("paper")
        val inserted = repo.insert(container).block()!!

        assertEquals(container.name, inserted.name)
    }

    @Test
    fun `delete should remove a previously inserted record`() {
        val restaurant = repo.insert(TakeoutContainerCreate("bears")).block()!!

        repo.delete(restaurant.id).test()
            .expectNext(1).verifyComplete()
        repo.findAll().test()
            .verifyComplete()
    }

    @Test
    fun `delete should return 0 when no rows are deleted`() {
        repo.delete(8).test()
            .expectNext(0).verifyComplete()
    }

    @Test
    fun `deleteAll should remove everything in the table`() {
        repo.insert(TakeoutContainerCreate("paper")).block()
        repo.insert(TakeoutContainerCreate("paper")).block()

        repo.deleteAll().test()
            .expectNext(2).verifyComplete()
        repo.findAll().test()
            .verifyComplete()
    }
}
