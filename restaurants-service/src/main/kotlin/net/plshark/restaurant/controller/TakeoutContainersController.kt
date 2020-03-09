package net.plshark.restaurant.controller

import net.plshark.restaurant.exception.NotFoundException
import net.plshark.restaurant.model.TakeoutContainer
import net.plshark.restaurant.repository.TakeoutContainersRepository
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/takeout-containers")
class TakeoutContainersController(private val repository: TakeoutContainersRepository) {

    @PostMapping
    fun create(@RequestBody container: TakeoutContainer): Mono<TakeoutContainer> {
        return repository.insert(container.copy(id = null))
    }

    @GetMapping
    fun findAll(): Flux<TakeoutContainer> {
        return repository.findAll()
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: Long): Mono<Void> {
        return repository.delete(id)
                .filter { i -> i == 0 }
                .flatMap { Mono.error<Any> { NotFoundException("No takeout container found for ID $id") } }
                .then()
    }
}
