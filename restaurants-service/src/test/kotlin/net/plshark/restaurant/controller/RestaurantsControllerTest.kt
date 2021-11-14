package net.plshark.restaurant.controller

import io.mockk.every
import io.mockk.mockk
import net.plshark.restaurant.Restaurant
import net.plshark.restaurant.RestaurantCreate
import net.plshark.restaurant.TakeoutContainer
import net.plshark.restaurant.exception.NotFoundException
import net.plshark.restaurant.repository.RestaurantContainersRepository
import net.plshark.restaurant.repository.RestaurantsRepository
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test
import reactor.kotlin.test.verifyError

@Suppress("ReactiveStreamsUnusedPublisher")
class RestaurantsControllerTest {

    private val repo = mockk<RestaurantsRepository>()
    private val restaurantContainersRepository = mockk<RestaurantContainersRepository>()
    private val controller = RestaurantsController(repo, restaurantContainersRepository)

    @Test
    fun `create should save the restaurant and the associated containers`() {
        val create = RestaurantCreate("test", "italian", null, listOf(1, 2))
        val restaurant = Restaurant(321L, "test", "italian", null, emptyList())
        every { repo.insert(create) } returns restaurant.toMono()
        every { restaurantContainersRepository.insert(321, match { it == 1L || it == 2L }) } returns
                Mono.just(1)
        every { restaurantContainersRepository.getContainersForRestaurant(321) } returns
                Flux.just(TakeoutContainer(1, "1"), TakeoutContainer(2, "2"))

        controller.create(create).test()
            .expectNext(restaurant.copy(containers = listOf(TakeoutContainer(1, "1"), TakeoutContainer(2, "2"))))
            .verifyComplete()
    }

    @Test
    fun `findById should return the matching object`() {
        val match = Restaurant(321L, "test", "chinese", "1234 street", emptyList())
        every { repo.findById(321) } returns match.toMono()
        every { restaurantContainersRepository.getContainersForRestaurant(321) } returns
                Flux.just(TakeoutContainer(123, "paper"))

        controller.findById(321).test()
            .expectNext(match.copy(containers = listOf(TakeoutContainer(123, "paper"))))
            .verifyComplete()
    }

    @Test
    fun `findById should throw a NotFoundException if no match is found`() {
        every { repo.findById(123) } returns Mono.empty()

        controller.findById(123).test()
            .verifyError(NotFoundException::class)
    }

    @Test
    fun `update should update the restaurant and the container types`() {
        val request = Restaurant(1, "arbys", "burgers", null,
            listOf(TakeoutContainer(5, "styrofoam"), TakeoutContainer(4, "paper")))
        every { repo.update(request) } returns 1.toMono()
        every { restaurantContainersRepository.getContainersForRestaurant(1) }.returnsMany(
            Flux.just(TakeoutContainer(3, "plastic"), TakeoutContainer(4, "paper")),
            Flux.just(TakeoutContainer(5, "styrofoam"), TakeoutContainer(4, "paper")))
        every { restaurantContainersRepository.delete(3) } returns Mono.just(1)
        every { restaurantContainersRepository.insert(1, 5) } returns Mono.just(1)

        controller.update(1, request).test()
            .expectNext(request)
            .verifyComplete()
    }

    @Test
    fun `update should use the ID in the path and ignore the ID in the request body`() {
        val request = Restaurant(1, "arbys", "fast food", null, emptyList())
        val expected = Restaurant(5, "arbys", "fast food", null, emptyList())
        every { repo.update(expected) } returns 1.toMono()
        every { restaurantContainersRepository.getContainersForRestaurant(5) } returns Flux.empty()

        controller.update(5, request).test()
            .expectNext(expected)
            .verifyComplete()
    }

    @Test
    fun `update should return a NotFoundException if no record is updated`() {
        val request = Restaurant(1, "arbys", "burgers", null, emptyList())
        every { repo.update(any()) } returns 0.toMono()
        every { restaurantContainersRepository.getContainersForRestaurant(any()) } returns Flux.empty()

        controller.update(1, request).test()
            .verifyError(NotFoundException::class)
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
