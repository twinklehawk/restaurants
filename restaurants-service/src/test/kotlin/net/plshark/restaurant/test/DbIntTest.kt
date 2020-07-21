package net.plshark.restaurant.test

import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import org.junit.jupiter.api.BeforeEach
import org.springframework.data.r2dbc.core.DatabaseClient

open class DbIntTest : IntTest() {

    protected lateinit var connectionFactory: ConnectionFactory
    protected lateinit var databaseClient: DatabaseClient

    @BeforeEach
    fun setupConnection() {
        connectionFactory =
            ConnectionFactories.get("r2dbc:postgresql://test_user:test_user_pass@localhost:5432/postgres?schema=restaurants")
        databaseClient = DatabaseClient.create(connectionFactory)
    }
}
