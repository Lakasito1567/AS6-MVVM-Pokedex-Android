import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

    private val repository = PokemonRepository()

    val pokemons: LiveData<List<Pokemon>> = repository.pokemons

    private val _selected = MutableLiveData<Pokemon?>()
    val selected: LiveData<Pokemon?> = _selected

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
