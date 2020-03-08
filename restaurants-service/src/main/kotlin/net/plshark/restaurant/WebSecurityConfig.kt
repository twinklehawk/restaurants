package net.plshark.restaurant

import net.plshark.users.auth.jwt.HttpBearerBuilder
import net.plshark.users.auth.jwt.JwtReactiveAuthenticationManager
import net.plshark.users.auth.service.AuthService
import net.plshark.users.auth.service.AuthServiceClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import org.springframework.web.reactive.function.client.WebClient

@EnableWebFluxSecurity
class WebSecurityConfig {

    /**
     * Set up the security filter chain
     * @param http the spring http security configurer
     * @return the filter chain
     */
    @Bean
    fun securityFilterChain(http: ServerHttpSecurity, authenticationManager: JwtReactiveAuthenticationManager): SecurityWebFilterChain {
        val builder = HttpBearerBuilder(authenticationManager)
        return http
                .authorizeExchange()
                .anyExchange()
                .hasRole("takeout-user")
                .and()
                .authenticationManager(authenticationManager)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .csrf().disable()
                .logout().disable()
                .addFilterAt(builder.buildFilter(), SecurityWebFiltersOrder.HTTP_BASIC)
                .build()
    }

    @Bean
    fun authenticationManager(authService: AuthService): JwtReactiveAuthenticationManager {
        return JwtReactiveAuthenticationManager(authService)
    }

    @Bean
    fun authService(webClient: WebClient, @Value("\${plshark.auth.url}") baseUri: String): AuthService {
        return AuthServiceClient(webClient, baseUri)
    }
}
