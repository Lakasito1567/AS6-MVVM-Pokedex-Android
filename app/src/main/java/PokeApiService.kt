import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

data class PokemonListResponse(val results: List<ResultDto>)
data class ResultDto(val name: String, val url: String)

data class PokemonDetailResponse(
    val id: Int,
    val name: String,
    val sprites: SpritesDto,
    val types: List<TypeSlotDto>
)
data class SpritesDto(val front_default: String?)
data class TypeSlotDto(val type: TypeDto)
data class TypeDto(val name: String)

interface PokeApiService {
    @GET("pokemon")
    suspend fun getPokemonList(@Query("limit") limit: Int, @Query("offset") offset: Int = 0): PokemonListResponse

    @GET("pokemon/{idOrName}")
    suspend fun getPokemonDetail(@Path("idOrName") idOrName: String): PokemonDetailResponse
}