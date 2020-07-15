package net.plshark.restaurant.controller

import net.plshark.restaurant.CreateTakeoutContainer
import net.plshark.restaurant.exception.NotFoundException
import net.plshark.restaurant.TakeoutContainer
import net.plshark.restaurant.TakeoutContainersService
import net.plshark.restaurant.repository.TakeoutContainersRepository
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/takeout-containers")
class TakeoutContainersController(private val repository: TakeoutContainersRepository) : TakeoutContainersService {

    @PostMapping
    override fun create(@RequestBody container: CreateTakeoutContainer): Mono<TakeoutContainer> {
        return repository.insert(container)
    }

    @GetMapping
    override fun findAll(): Flux<TakeoutContainer> {
        return repository.findAll()
    }

    @DeleteMapping("/{id}")
    override fun delete(@PathVariable("id") id: Long): Mono<Void> {
        return repository.delete(id)
            .filter { i -> i == 0 }
            .flatMap { Mono.error<Any> { NotFoundException("No takeout container found for ID $id") } }
            .then()
    }
}
