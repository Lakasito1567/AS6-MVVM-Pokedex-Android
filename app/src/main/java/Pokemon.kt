
data class Pokemon(
    val id: Int,
    val name: String,
    val type: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val isFavorite: Boolean = false
)
