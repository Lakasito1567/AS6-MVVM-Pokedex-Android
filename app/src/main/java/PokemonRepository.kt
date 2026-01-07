
import Pokemon
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class PokemonRepository {

    private val initialList = mutableListOf(
        Pokemon(1, "Bulbasaur", "Grass/Poison", "Seed Pokémon"),
        Pokemon(2, "Charmander", "Fire", "Lizard Pokémon"),
        Pokemon(3, "Squirtle", "Water", "Tiny Turtle Pokémon"),
        Pokemon(4, "Pikachu", "Electric", "Mouse Pokémon")
    )

    private val _pokemons = MutableLiveData<List<Pokemon>>(initialList.toList())
    val pokemons: LiveData<List<Pokemon>> = _pokemons

    fun delete(pokemon: Pokemon) {
        initialList.removeAll { it.id == pokemon.id }
        _pokemons.value = initialList.toList()
    }

    fun getById(id: Int): Pokemon? = initialList.find { it.id == id }

    fun setFavorite(id: Int, fav: Boolean) {
        val updated = initialList.map { p ->
            if (p.id == id) p.copy(isFavorite = fav) else p
        }
        initialList.clear()
        initialList.addAll(updated)
        _pokemons.value = initialList.toList()
    }
}
