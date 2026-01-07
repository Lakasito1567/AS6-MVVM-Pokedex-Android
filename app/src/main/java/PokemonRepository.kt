import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class PokemonRepository {

    private val _pokemons = MutableLiveData<List<Pokemon>>(emptyList())
    val pokemons: LiveData<List<Pokemon>> = _pokemons

    private val internalList = mutableListOf<Pokemon>()

    suspend fun fetchFromApi(limit: Int = 50) {
        try {
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
                    name = d.name.replaceFirstChar { it.uppercase() },
                    type = types,
                    description = "",
                    imageUrl = d.sprites.front_default ?: "",
                    isFavorite = false
                )
            }

            internalList.clear()
            internalList.addAll(mapped)
            _pokemons.postValue(internalList.toList())
        } catch (e: Exception) {
            e.printStackTrace()
            _pokemons.postValue(emptyList())
        }
    }

    fun delete(pokemon: Pokemon) {
        internalList.removeAll { it.id == pokemon.id }
        _pokemons.value = internalList.toList()
    }

    fun getById(id: Int): Pokemon? = internalList.find { it.id == id }

    fun setFavorite(id: Int, fav: Boolean) {
        val updated = internalList.map { p -> if (p.id == id) p.copy(isFavorite = fav) else p }
        internalList.clear()
        internalList.addAll(updated)
        _pokemons.value = internalList.toList()
    }
}