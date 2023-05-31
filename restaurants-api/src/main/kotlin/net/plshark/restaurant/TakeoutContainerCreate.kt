package net.plshark.restaurant

data class TakeoutContainerCreate(val name: String) {
    /**
     * Build a [TakeoutContainer] from this object
     * @param id the ID to use
     * @return the takeout container
     */
    fun toTakeoutContainer(id: Long) =
        TakeoutContainer(
            id = id,
            name = name,
        )
}
