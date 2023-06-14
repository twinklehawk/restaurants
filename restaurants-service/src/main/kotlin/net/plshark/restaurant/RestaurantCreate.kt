package net.plshark.restaurant

data class RestaurantCreate(
    val name: String,
    val type: String,
    val address: String?,
    val containerIds: List<Long>,
) {
    /**
     * Build a [Restaurant] from this object with an empty containers list
     * @param id the ID to use
     * @return the restaurant
     */
    fun toRestaurant(id: Long) =
        Restaurant(
            id = id,
            name = name,
            type = type,
            address = address,
            containers = emptyList(),
        )
}
