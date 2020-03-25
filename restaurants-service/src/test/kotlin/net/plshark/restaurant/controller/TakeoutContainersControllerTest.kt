package net.plshark.restaurant.controller

import io.mockk.every
import io.mockk.mockk
import net.plshark.restaurant.CreateTakeoutContainer
import net.plshark.restaurant.exception.NotFoundException
import net.plshark.restaurant.TakeoutContainer
import net.plshark.restaurant.repository.TakeoutContainersRepository
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@Suppress("ReactorUnusedPublisher")
class TakeoutContainersControllerTest {

    private val repo = mockk<TakeoutContainersRepository>()
    private val controller = TakeoutContainersController(repo)

    @Test
    fun `create should save the container`() {
        val inserted = TakeoutContainer(321L, "test")
        every { repo.insert(match { it.name == "test" }) } returns Mono.just(inserted)

        StepVerifier.create(controller.create(CreateTakeoutContainer("test")))
                .expectNext(inserted)
                .verifyComplete()
    }

    @Test
    fun `findAll should return all results from the repo`() {
        every { repo.findAll() } returns Flux.just(
            TakeoutContainer(
                1,
                "paper"
            ), TakeoutContainer(2, "plastic")
        )

        StepVerifier.create(controller.findAll())
                .expectNext(TakeoutContainer(1, "paper"))
                .expectNext(TakeoutContainer(2, "plastic"))
                .verifyComplete()
    }

    @Test
    fun `delete should send the ID to the repo`() {
        every {repo.delete(8) } returns Mono.just(1)

        StepVerifier.create(controller.delete(8))
                .verifyComplete()
    }

    @Test
    fun `delete should return a NotFoundException if no record is deleted`() {
        every { repo.delete(8) } returns Mono.just(0)

        StepVerifier.create(controller.delete(8))
                .verifyError(NotFoundException::class.java)
    }
}
