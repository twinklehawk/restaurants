package net.plshark.restaurant.repository

import java.time.OffsetDateTime
import io.r2dbc.spi.ConnectionFactories
import net.plshark.restaurant.model.Restaurant
import org.springframework.data.r2dbc.core.DatabaseClient
import reactor.test.StepVerifier
import spock.lang.Requires
import spock.lang.Specification

@Requires({ System.getProperty('runIntTests') != null })
class RestaurantsRepositoryIntTest extends Specification {

    RestaurantsRepository repo

    def setup() {
        def connectionFactory = ConnectionFactories.get("r2dbc:postgresql://test_user:test_user_pass@localhost:5432/postgres?schema=restaurants")
        repo = new RestaurantsRepository(DatabaseClient.create(connectionFactory))
    }

    def cleanup() {
        repo.deleteAll().block()
    }

    def 'insert should return the inserted object with the generated ID set'() {
        def restaurant = new Restaurant("bears", "paper", OffsetDateTime.now())

        when:
        def inserted = repo.insert(restaurant).block()

        then:
        inserted.id != null
        inserted.name == restaurant.name
        inserted.containerType == restaurant.containerType
        inserted.createTime == restaurant.createTime
    }

    def 'findById should return a previously inserted record'() {
        def restaurant = repo.insert(new Restaurant("bears", "paper", OffsetDateTime.now())).block()

        expect:
        StepVerifier.create(repo.findById(restaurant.id))
                .expectNext(restaurant)
                .verifyComplete()
    }

    def 'findById should return empty when no record matches'() {
        expect:
        StepVerifier.create(repo.findById(18))
                .verifyComplete()
    }

    def 'findByName should return the matching records'() {
        def restaurant1 = repo.insert(new Restaurant("bears", "paper", OffsetDateTime.now())).block()
        repo.insert(new Restaurant("cows", "styrofoam", OffsetDateTime.now())).block()
        def restaurant3 = repo.insert(new Restaurant("bears", "styrofoam", OffsetDateTime.now())).block()

        expect:
        StepVerifier.create(repo.findByName('bears'))
                .expectNext(restaurant1)
                .expectNext(restaurant3)
                .verifyComplete()
    }

    def 'findByName should return empty when there are no matches'() {
        expect:
        StepVerifier.create(repo.findByName('reindeer'))
                .verifyComplete()
    }

    def 'update should set the name and takeout type'() {
        def restaurant = repo.insert(new Restaurant("bears", "paper", OffsetDateTime.now())).block()
        def update = new Restaurant(restaurant.id, 'beets', 'rocks', restaurant.createTime)

        expect:
        StepVerifier.create(repo.update(update))
                .expectNext(1).verifyComplete()
        StepVerifier.create(repo.findById(restaurant.id))
                .expectNext(update).verifyComplete()
    }

    def 'update should throw an error if the restaurant does not have an ID'() {
        when:
        repo.update(new Restaurant("bears", "paper"))

        then:
        thrown(NullPointerException)
    }

    def 'delete should remove a previously inserted record'() {
        def restaurant = repo.insert(new Restaurant("bears", "paper", OffsetDateTime.now())).block()

        expect:
        StepVerifier.create(repo.delete(restaurant.id))
                .expectNext(1).verifyComplete()
        StepVerifier.create(repo.findById(restaurant.id))
                .verifyComplete()
    }

    def 'delete should return 0 when no rows are deleted'() {
        expect:
        StepVerifier.create(repo.delete(8))
                .expectNext(0).verifyComplete()
    }

    def 'deleteAll should remove everything in the table'() {
        repo.insert(new Restaurant("bears", "paper", OffsetDateTime.now())).block()
        repo.insert(new Restaurant("beets", "paper", OffsetDateTime.now())).block()

        expect:
        StepVerifier.create(repo.deleteAll())
                .expectNext(2).verifyComplete()
        StepVerifier.create(repo.findAll(100, 0))
                .verifyComplete()
    }
}
