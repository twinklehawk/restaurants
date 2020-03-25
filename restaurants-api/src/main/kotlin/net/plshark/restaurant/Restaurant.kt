package net.plshark.restaurant

import java.time.OffsetDateTime

data class Restaurant(val id: Long, val name: String, val containerType: String, val createTime: OffsetDateTime)
