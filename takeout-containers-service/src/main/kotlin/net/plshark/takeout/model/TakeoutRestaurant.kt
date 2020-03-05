package net.plshark.takeout.model

import org.springframework.data.annotation.Id
import java.time.OffsetDateTime

data class TakeoutRestaurant(@Id val id: Long?, val name: String, val containerType: String,
                             val createTime: OffsetDateTime?)
