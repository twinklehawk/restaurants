package net.plshark.restaurant

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RestaurantCreateTest {

    @Test
    fun `toRestaurant should copy the object data into a restaurant with the provided ID`() {
        val create = RestaurantCreate("test", "type", "addr", listOf(321))
        assertEquals(
            Restaurant(123, "test", "type", "addr", emptyList()),
            create.toRestaurant(123),
        )
    }
}
