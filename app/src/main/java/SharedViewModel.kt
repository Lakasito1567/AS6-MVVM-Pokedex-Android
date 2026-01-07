import androidx.lifecycle.*
import kotlinx.coroutines.launch

class SharedViewModel : ViewModel() {

    private val repository = PokemonRepository()

    val pokemons: LiveData<List<Pokemon>> = repository.pokemons

    private val _selected = MutableLiveData<Pokemon?>()
    val selected: LiveData<Pokemon?> = _selected

    init {
        loadPokemonsFromApi()
    }

    fun loadPokemonsFromApi(limit: Int = 30) {
        viewModelScope.launch {
            try {
                repository.fetchFromApi(limit)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun select(p: Pokemon) { _selected.value = p }

    fun delete(p: Pokemon) {
        repository.delete(p)
        if (_selected.value?.id == p.id) _selected.value = null
    }

    fun setFavorite(id: Int, fav: Boolean) {
        repository.setFavorite(id, fav)
        _selected.value = repository.getById(id)
    }
}
