package net.plshark.restaurant

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TakeoutContainerCreateTest {

    @Test
    fun `toTakeoutContainer should copy the object data into a takeout container with the provided ID`() {
        val create = TakeoutContainerCreate("test container")
        assertEquals(TakeoutContainer(123, "test container"), create.toTakeoutContainer(123))
    }
}
