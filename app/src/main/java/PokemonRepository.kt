import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class PokemonRepository {

    private val initialList = mutableListOf<Pokemon>()
    private val _pokemons = androidx.lifecycle.MutableLiveData<List<Pokemon>>(initialList.toList())
    val pokemons: androidx.lifecycle.LiveData<List<Pokemon>> = _pokemons

    suspend fun fetchFromApi(limit: Int = 50) {
        val api = RetrofitClient.api
        val listResponse = api.getPokemonList(limit)
        val details = coroutineScope {
            listResponse.results.map { res ->
                async { api.getPokemonDetail(res.name) }
            }.map { it.await() }
        }

        val mapped = details.map { d ->
            val types = d.types.joinToString(",") { it.type.name }
            Pokemon(
                id = d.id,
                name = d.name.capitalize(),
                type = types,
                description = "",
                imageUrl = d.sprites.front_default ?: "",
                isFavorite = false
            )
        }

        initialList.clear()
        initialList.addAll(mapped)
        _pokemons.postValue(initialList.toList())
    }

    fun delete(pokemon: Pokemon) {
        initialList.removeAll { it.id == pokemon.id }
        _pokemons.value = initialList.toList()
    }

    fun getById(id: Int): Pokemon? = initialList.find { it.id == id }

    fun setFavorite(id: Int, fav: Boolean) {
        val updated = initialList.map { p -> if (p.id == id) p.copy(isFavorite = fav) else p }
        initialList.clear()
        initialList.addAll(updated)
        _pokemons.value = initialList.toList()
    }
}