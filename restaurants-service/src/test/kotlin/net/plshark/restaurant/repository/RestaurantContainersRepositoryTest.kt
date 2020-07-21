package net.plshark.restaurant.repository

import net.plshark.restaurant.RestaurantCreate
import net.plshark.restaurant.TakeoutContainerCreate
import net.plshark.restaurant.test.DbIntTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier

internal class RestaurantContainersRepositoryTest : DbIntTest() {

    private lateinit var repo: RestaurantContainersRepository
    private lateinit var restaurantsRepository: RestaurantsRepository
    private lateinit var containersRepository: TakeoutContainersRepository

    @BeforeEach
    fun setup() {
        repo = RestaurantContainersRepository(databaseClient)
        restaurantsRepository = RestaurantsRepository(databaseClient)
        containersRepository = TakeoutContainersRepository(databaseClient)
    }

    @AfterEach
    fun cleanup() {
        repo.deleteAll()
            .then(restaurantsRepository.deleteAll())
            .then(containersRepository.deleteAll())
            .block()
    }

    @Test
    fun `insert should save a restaurant-container association`() {
        val restaurant = restaurantsRepository.insert(RestaurantCreate("test restaurant", "test", null, emptyList()))
            .block()!!
        val container = containersRepository.insert(TakeoutContainerCreate("paper")).block()!!

        StepVerifier.create(repo.insert(restaurant.id, container.id))
            .expectNextCount(1)
            .verifyComplete()
        StepVerifier.create(repo.getContainersForRestaurant(restaurant.id))
            .expectNext(container)
            .verifyComplete()
    }

    @Test
    fun `getContainersForRestaurant should return empty when there are no associations`() {
        val restaurant = restaurantsRepository.insert(RestaurantCreate("test restaurant", "test", null, emptyList()))
            .block()!!

        StepVerifier.create(repo.getContainersForRestaurant(restaurant.id))
            .verifyComplete()
    }

    @Test
    fun `getContainersForRestaurant should return all associations for the restaurant`() {
        val restaurant = restaurantsRepository.insert(RestaurantCreate("test restaurant", "test", null, emptyList()))
            .block()!!
        val restaurant2 = restaurantsRepository.insert(RestaurantCreate("test restaurant 2", "test", null, emptyList()))
            .block()!!
        val paper = containersRepository.insert(TakeoutContainerCreate("paper")).block()!!
        val plastic = containersRepository.insert(TakeoutContainerCreate("plastic")).block()!!
        val sandstone = containersRepository.insert(TakeoutContainerCreate("sandstone")).block()!!

        repo.insert(restaurant.id, paper.id)
            .and(repo.insert(restaurant.id, sandstone.id))
            .and(repo.insert(restaurant2.id, plastic.id))
            .block()

        StepVerifier.create(repo.getContainersForRestaurant(restaurant.id))
            .expectNext(paper, sandstone)
            .verifyComplete()
    }

    @Test
    fun `delete should delete the single row`() {
        val restaurant = restaurantsRepository.insert(RestaurantCreate("test restaurant", "test", null, emptyList()))
            .block()!!
        val paper = containersRepository.insert(TakeoutContainerCreate("paper")).block()!!
        val plastic = containersRepository.insert(TakeoutContainerCreate("plastic")).block()!!

        repo.insert(restaurant.id, paper.id).block()!!
        val plasticId = repo.insert(restaurant.id, plastic.id).block()!!

        StepVerifier.create(repo.delete(plasticId))
            .expectNext(1)
            .verifyComplete()
        StepVerifier.create(repo.delete(plasticId))
            .expectNext(0)
            .verifyComplete()
        StepVerifier.create(repo.getContainersForRestaurant(restaurant.id))
            .expectNext(paper)
            .verifyComplete()
    }

    @Test
    fun `deleteAll should delete everything`() {
        val restaurant = restaurantsRepository.insert(RestaurantCreate("test restaurant", "test", null, emptyList()))
            .block()!!
        val paper = containersRepository.insert(TakeoutContainerCreate("paper")).block()!!
        val plastic = containersRepository.insert(TakeoutContainerCreate("plastic")).block()!!

        repo.insert(restaurant.id, paper.id).and(repo.insert(restaurant.id, plastic.id)).block()

        StepVerifier.create(repo.deleteAll())
            .expectNext(2)
            .verifyComplete()
        StepVerifier.create(repo.deleteAll())
            .expectNext(0)
            .verifyComplete()
        StepVerifier.create(repo.getContainersForRestaurant(restaurant.id))
            .verifyComplete()
    }
}
