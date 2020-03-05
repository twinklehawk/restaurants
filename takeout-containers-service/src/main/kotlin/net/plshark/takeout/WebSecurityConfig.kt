package net.plshark.takeout

import net.plshark.users.auth.jwt.HttpBearerBuilder
import net.plshark.users.auth.jwt.JwtReactiveAuthenticationManager
import net.plshark.users.auth.service.AuthService
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository

@EnableWebFluxSecurity
class WebSecurityConfig(val authService: AuthService) {

    /**
     * Set up the security filter chain
     * @param http the spring http security configurer
     * @return the filter chain
     */
    @Bean
    fun securityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        val builder = HttpBearerBuilder(authenticationManager())
        return http
                .authorizeExchange()
                .anyExchange()
                .hasRole("takeout-user")
                .and()
                .authenticationManager(authenticationManager())
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .csrf().disable()
                .logout().disable()
                .addFilterAt(builder.buildFilter(), SecurityWebFiltersOrder.HTTP_BASIC)
                .build()
    }

    @Bean
    fun authenticationManager(): JwtReactiveAuthenticationManager {
        return JwtReactiveAuthenticationManager(authService)
    }
}
