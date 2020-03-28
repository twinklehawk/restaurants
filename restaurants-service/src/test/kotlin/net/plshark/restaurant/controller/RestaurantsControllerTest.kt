package net.plshark.restaurant.controller

import io.mockk.every
import io.mockk.mockk
import net.plshark.restaurant.CreateRestaurant
import java.time.OffsetDateTime
import net.plshark.restaurant.exception.NotFoundException
import net.plshark.restaurant.Restaurant
import net.plshark.restaurant.repository.RestaurantsRepository
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@Suppress("ReactorUnusedPublisher")
class RestaurantsControllerTest {

    private val repo = mockk<RestaurantsRepository>()
    private val controller = RestaurantsController(repo)

    @Test
    fun `create should set the create time and save the restaurant`() {
        val inserted = Restaurant(321L, "test", "plastic", OffsetDateTime.now())
        every { repo.insert(match { it.name == "test" && it.containerType == "plastic" }) } returns Mono.just(inserted)

        StepVerifier.create(controller.create(CreateRestaurant("test", "plastic")))
            .expectNext(inserted)
            .verifyComplete()
    }

    @Test
    fun `findById should return the matching object`() {
        val match = Restaurant(321L, "test", "plastic", OffsetDateTime.now())
        every { repo.findById(321) } returns Mono.just(match)

        StepVerifier.create(controller.findById(321))
            .expectNext(match)
            .verifyComplete()
    }

    @Test
    fun `findById should throw a NotFoundException if no match is found`() {
        every { repo.findById(123) } returns Mono.empty()

        StepVerifier.create(controller.findById(123))
            .verifyError(NotFoundException::class.java)
    }

    @Test
    fun `update should send the parsed request body to the repo`() {
        val request = Restaurant(1, "arbys", "plastic", OffsetDateTime.now())
        every { repo.update(request) } returns Mono.just(1)

        StepVerifier.create(controller.update(1, request))
            .expectNext(request)
            .verifyComplete()
    }

    @Test
    fun `update should use the ID in the path and ignore the ID in the request body`() {
        val request = Restaurant(1, "arbys", "plastic", OffsetDateTime.now())
        val expected = Restaurant(5, "arbys", "plastic", request.createTime)
        every { repo.update(expected) } returns Mono.just(1)

        StepVerifier.create(controller.update(5, request))
            .expectNext(expected)
            .verifyComplete()
    }

    @Test
    fun `update should return a NotFoundException if no record is updated`() {
        val request = Restaurant(1, "arbys", "plastic", OffsetDateTime.now())
        every { repo.update(any()) } returns Mono.just(0)

        StepVerifier.create(controller.update(1, request))
            .verifyError(NotFoundException::class.java)
    }

    @Test
    fun `delete should send the ID to the repo`() {
        every { repo.delete(8) } returns Mono.just(1)

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
