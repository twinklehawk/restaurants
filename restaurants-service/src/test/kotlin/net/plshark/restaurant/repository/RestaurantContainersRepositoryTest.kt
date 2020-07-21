package net.plshark.restaurant.repository

import net.plshark.restaurant.RestaurantCreate
import net.plshark.restaurant.TakeoutContainerCreate
import net.plshark.restaurant.test.DbIntTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.kotlin.test.test

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

        repo.insert(restaurant.id, container.id).test()
            .expectNextCount(1)
            .verifyComplete()
        repo.getContainersForRestaurant(restaurant.id).test()
            .expectNext(container)
            .verifyComplete()
    }

    @Test
    fun `getContainersForRestaurant should return empty when there are no associations`() {
        val restaurant = restaurantsRepository.insert(RestaurantCreate("test restaurant", "test", null, emptyList()))
            .block()!!

        repo.getContainersForRestaurant(restaurant.id).test()
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

        repo.getContainersForRestaurant(restaurant.id).test()
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

        repo.delete(plasticId).test()
            .expectNext(1)
            .verifyComplete()
        repo.delete(plasticId).test()
            .expectNext(0)
            .verifyComplete()
        repo.getContainersForRestaurant(restaurant.id).test()
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

        repo.deleteAll().test()
            .expectNext(2)
            .verifyComplete()
        repo.deleteAll().test()
            .expectNext(0)
            .verifyComplete()
        repo.getContainersForRestaurant(restaurant.id).test()
            .verifyComplete()
    }
}
