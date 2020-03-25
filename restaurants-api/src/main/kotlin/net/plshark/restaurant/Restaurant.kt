package net.plshark.restaurant

import java.time.OffsetDateTime

data class Restaurant(val id: Long?, val name: String, val containerType: String, val createTime: OffsetDateTime?) {
    constructor(name: String, containerType: String) : this(null, name, containerType, null)
    constructor(name: String, containerType: String, createTime: OffsetDateTime?) : this(null, name, containerType, createTime)
}
