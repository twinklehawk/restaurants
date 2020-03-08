package net.plshark.restaurant.model

import org.springframework.data.annotation.Id
import java.time.OffsetDateTime

data class Restaurant(@Id val id: Long?, val name: String, val containerType: String, val createTime: OffsetDateTime?)
