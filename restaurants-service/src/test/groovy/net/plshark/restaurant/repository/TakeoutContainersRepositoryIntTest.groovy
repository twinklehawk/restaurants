package net.plshark.restaurant.repository


import io.r2dbc.spi.ConnectionFactories
import net.plshark.restaurant.model.TakeoutContainer
import org.springframework.data.r2dbc.core.DatabaseClient
import reactor.test.StepVerifier
import spock.lang.Requires
import spock.lang.Specification

@Requires({ System.getProperty('runIntTests') != null })
class TakeoutContainersRepositoryIntTest extends Specification {

    TakeoutContainersRepository repo

    def setup() {
        def connectionFactory = ConnectionFactories.get("r2dbc:postgresql://test_user:test_user_pass@localhost:5432/postgres?schema=restaurants")
        repo = new TakeoutContainersRepository(DatabaseClient.create(connectionFactory))
    }

    def cleanup() {
        repo.deleteAll().block()
    }

    def 'insert should return the inserted object with the generated ID set'() {
        def container = new TakeoutContainer("paper")

        when:
        def inserted = repo.insert(container).block()

        then:
        inserted.id != null
        inserted.name == container.name
    }

    def 'delete should remove a previously inserted record'() {
        def restaurant = repo.insert(new TakeoutContainer("bears")).block()

        expect:
        StepVerifier.create(repo.delete(restaurant.id))
                .expectNext(1).verifyComplete()
        StepVerifier.create(repo.findAll())
                .verifyComplete()
    }

    def 'delete should return 0 when no rows are deleted'() {
        expect:
        StepVerifier.create(repo.delete(8))
                .expectNext(0).verifyComplete()
    }

    def 'deleteAll should remove everything in the table'() {
        repo.insert(new TakeoutContainer("paper")).block()
        repo.insert(new TakeoutContainer("paper")).block()

        expect:
        StepVerifier.create(repo.deleteAll())
                .expectNext(2).verifyComplete()
        StepVerifier.create(repo.findAll())
                .verifyComplete()
    }
}
