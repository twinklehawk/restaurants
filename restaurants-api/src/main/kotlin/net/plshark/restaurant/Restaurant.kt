package net.plshark.restaurant

data class Restaurant(
    val id: Long,
    val name: String,
    val type: String,
    val address: String?,
    val containers: List<TakeoutContainer>,
)
