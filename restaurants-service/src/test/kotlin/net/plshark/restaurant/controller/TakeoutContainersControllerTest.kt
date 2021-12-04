package net.plshark.restaurant.controller

import io.mockk.every
import io.mockk.mockk
import net.plshark.restaurant.TakeoutContainer
import net.plshark.restaurant.TakeoutContainerCreate
import net.plshark.restaurant.exception.NotFoundException
import net.plshark.restaurant.repository.TakeoutContainersRepository
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test
import reactor.kotlin.test.verifyError

@Suppress("ReactiveStreamsUnusedPublisher")
class TakeoutContainersControllerTest {

    private val repo = mockk<TakeoutContainersRepository>()
    private val controller = TakeoutContainersController(repo)

    @Test
    fun `create should save the container`() {
        val inserted = TakeoutContainer(321L, "test")
        every { repo.insert(match { it.name == "test" }) } returns inserted.toMono()

        controller.create(TakeoutContainerCreate("test")).test()
            .expectNext(inserted)
            .verifyComplete()
    }

    @Test
    fun `findAll should return all results from the repo`() {
        every { repo.findAll() } returns Flux.just(
            TakeoutContainer(1, "paper"),
            TakeoutContainer(2, "plastic")
        )

        controller.findAll().test()
            .expectNext(TakeoutContainer(1, "paper"))
            .expectNext(TakeoutContainer(2, "plastic"))
            .verifyComplete()
    }

    @Test
    fun `delete should send the ID to the repo`() {
        every { repo.delete(8) } returns 1.toMono()

        controller.delete(8).test()
            .verifyComplete()
    }

    @Test
    fun `delete should return a NotFoundException if no record is deleted`() {
        every { repo.delete(8) } returns 0.toMono()

        controller.delete(8).test()
            .verifyError(NotFoundException::class)
    }
}
