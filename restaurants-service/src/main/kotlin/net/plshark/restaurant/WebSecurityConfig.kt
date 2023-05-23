package net.plshark.restaurant

import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository

@EnableWebFluxSecurity
class WebSecurityConfig {

    /**
     * Set up the security filter chain
     * @param http the spring http security configurer
     * @return the filter chain
     */
    @Bean
    fun securityFilterChain(
        http: ServerHttpSecurity
    ): SecurityWebFilterChain {
        // TODO set up security
        return http
            .authorizeExchange {
                it.anyExchange()
                    .hasRole("takeout-user")
            }
            //.authenticationManager(authenticationManager)
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .csrf { it.disable() }
            .logout { it.disable() }
            //.httpBasic()
            .build()
    }
}
