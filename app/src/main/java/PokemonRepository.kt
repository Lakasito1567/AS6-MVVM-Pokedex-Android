import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class PokemonRepository {

    private val _pokemons = MutableLiveData<List<Pokemon>>(emptyList())
    val pokemons: LiveData<List<Pokemon>> = _pokemons

    private val internalList = mutableListOf<Pokemon>()
    private var offset = 0
    private val limit = 20
    var isLoading = false

    suspend fun fetchFromApi() {
        if (isLoading) return
        isLoading = true
        try {
            val api = RetrofitClient.api
            val listResponse = api.getPokemonList(limit = limit, offset = offset)

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
                    isDeleted = false
                )
            }

            internalList.addAll(mapped)
            _pokemons.postValue(internalList.toList())

            offset += limit
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    fun resetPagination() {
        offset = 0
        internalList.clear()
        _pokemons.value = emptyList()
    }

    fun delete(pokemon: Pokemon) {
        internalList.removeAll { it.id == pokemon.id }
        _pokemons.value = internalList.toList()
    }

    fun getById(id: Int): Pokemon? = internalList.find { it.id == id }

    fun moveToTrash(id: Int) {
        _pokemons.value = _pokemons.value?.map {
            if (it.id == id) it.copy(isDeleted = true) else it
        }
    }

    fun restoreFromTrash(id: Int) {
        _pokemons.value = _pokemons.value?.map {
            if (it.id == id) it.copy(isDeleted = false) else it
        }
    }

}