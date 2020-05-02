package net.plshark.restaurant.test

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceAccessMode
import org.junit.jupiter.api.parallel.ResourceLock

@Tag("integrationTest")
@Execution(ExecutionMode.SAME_THREAD)
@ResourceLock("integration", mode = ResourceAccessMode.READ_WRITE)
open class IntTest
