package net.plshark.restaurant.controller


import net.plshark.restaurant.exception.NotFoundException
import net.plshark.restaurant.model.TakeoutContainer
import net.plshark.restaurant.repository.TakeoutContainersRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification

class TakeoutContainersControllerTest extends Specification {

    def repo = Mock(TakeoutContainersRepository)
    def controller = new TakeoutContainersController(repo)

    def 'create should save the container'() {
        def inserted = new TakeoutContainer(321L, 'test')
        repo.insert({ c -> c.id == null && c.name == 'test' }) >>
                Mono.just(inserted)

        expect:
        StepVerifier.create(controller.create(new TakeoutContainer('test')))
                .expectNext(inserted)
                .verifyComplete()
    }

    def 'findAll should return all results from the repo'() {
        repo.findAll() >> Flux.just(new TakeoutContainer(1, 'paper'), new TakeoutContainer(2, 'plastic'))

        expect:
        StepVerifier.create(controller.findAll())
                .expectNext(new TakeoutContainer(1, 'paper'))
                .expectNext(new TakeoutContainer(2, 'plastic'))
                .verifyComplete()
    }

    def 'delete should send the ID to the repo'() {
        repo.delete(8) >> Mono.just(1)

        expect:
        StepVerifier.create(controller.delete(8))
                .verifyComplete()
    }

    def 'delete should return a NotFoundException if no record is deleted'() {
        repo.delete(8) >> Mono.just(0)

        expect:
        StepVerifier.create(controller.delete(8))
                .verifyError(NotFoundException)
    }
}
