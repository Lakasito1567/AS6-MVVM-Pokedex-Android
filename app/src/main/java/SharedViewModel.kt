import androidx.lifecycle.*
import kotlinx.coroutines.launch

class SharedViewModel : ViewModel() {

    private val repository = PokemonRepository()

    val pokemons: LiveData<List<Pokemon>> = repository.pokemons

    private val _selected = MediatorLiveData<Pokemon?>()
    val selected: LiveData<Pokemon?> = _selected

    val favoritePokemons = MediatorLiveData<List<Pokemon>>()

    init {
        favoritePokemons.addSource(pokemons) { list ->
            favoritePokemons.value = list.filter { it.isFavorite }
        }

        fetchPokemons()
    }

    private fun fetchPokemons() {
        viewModelScope.launch {
            repository.fetchFromApi()
        }
    }

    fun select(p: Pokemon) {
        _selected.value = p
    }

    fun delete(p: Pokemon) {
        repository.delete(p)
        if (_selected.value?.id == p.id) _selected.value = null
    }

    fun setFavorite(id: Int, fav: Boolean) {
        repository.setFavorite(id, fav)
        _selected.value = repository.getById(id)
    }
}