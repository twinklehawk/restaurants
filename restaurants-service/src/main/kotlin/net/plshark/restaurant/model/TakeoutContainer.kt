package net.plshark.restaurant.model

data class TakeoutContainer(val id: Long?, val name: String) {
    constructor(name: String) : this(null, name)
}
