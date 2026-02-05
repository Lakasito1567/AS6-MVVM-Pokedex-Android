import androidx.lifecycle.*
import kotlinx.coroutines.launch

class SharedViewModel : ViewModel() {

    private val repository = PokemonRepository()

    val pokemons: LiveData<List<Pokemon>> = repository.pokemons

    private val _selected = MediatorLiveData<Pokemon?>()
    val selected: LiveData<Pokemon?> = _selected

    private val _searchQuery = MutableLiveData<String>("")
    val searchQuery: LiveData<String> = _searchQuery

    // Exponer si está cargando para evitar llamadas duplicadas
    val isLoading: Boolean
        get() = repository.isLoading

    init {
        // Primera carga
        fetchPokemons()
    }

    // Función pública para cargar Pokémon (primera página o siguientes)
    fun fetchPokemons() {
        viewModelScope.launch {
            repository.fetchFromApi()
        }
    }

    // Resetear la lista y volver a cargar desde el inicio
    fun resetPokemons() {
        repository.resetPagination()
        fetchPokemons()
    }

    fun select(p: Pokemon) {
        _selected.value = p
    }

    fun delete(p: Pokemon) {
        repository.moveToTrash(p.id)
        if (_selected.value?.id == p.id) _selected.value = null
    }

    fun moveToTrash(p: Pokemon) {
        repository.moveToTrash(p.id)
    }

    fun restoreFromTrash(p: Pokemon) {
        repository.restoreFromTrash(p.id)
    }

    // Lista filtrada de eliminados
    fun getDeletedPokemons(): LiveData<List<Pokemon>> =
        MediatorLiveData<List<Pokemon>>().apply {
            addSource(pokemons) { list ->
                value = list?.filter { it.isDeleted }
            }
        }

    fun restore(p: Pokemon) {
        repository.restoreFromTrash(p.id)
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Lista filtrada para pantalla principal
    fun getFilteredPokemons(): LiveData<List<Pokemon>> =
        MediatorLiveData<List<Pokemon>>().apply {
            addSource(pokemons) { list -> value = filterList(list, _searchQuery.value) }
            addSource(_searchQuery) { query -> value = filterList(pokemons.value, query) }
        }

    // Lista filtrada solo de Pokémon no eliminados
    fun getFilteredActivePokemons(): LiveData<List<Pokemon>> =
        MediatorLiveData<List<Pokemon>>().apply {
            addSource(pokemons) { list ->
                val active = list?.filter { !it.isDeleted }
                value = filterList(active, _searchQuery.value)
            }
            addSource(_searchQuery) { query ->
                val active = pokemons.value?.filter { !it.isDeleted }
                value = filterList(active, query)
            }
        }

    private fun filterList(list: List<Pokemon>?, query: String?): List<Pokemon> {
        if (list.isNullOrEmpty() || query.isNullOrBlank()) return list ?: emptyList()
        return list.filter { it.name.contains(query, ignoreCase = true) }
    }
}
