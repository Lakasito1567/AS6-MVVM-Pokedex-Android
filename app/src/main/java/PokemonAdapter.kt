import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pokemon.databinding.ItemPokemonBinding
import com.bumptech.glide.Glide

class PokemonAdapter(
    private val onClick: (Pokemon) -> Unit,
    private val onStarClick: (Pokemon) -> Unit
) : ListAdapter<Pokemon, PokemonAdapter.PokemonViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val binding = ItemPokemonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PokemonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PokemonViewHolder(private val binding: ItemPokemonBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(p: Pokemon) {
            binding.tvName.text = p.name
            binding.tvType.text = p.type
            Glide.with(binding.ivImage.context)
                .load(p.imageUrl)
                .centerCrop()
                .into(binding.ivImage)

            binding.ivStar.setImageResource(
                if (p.isFavorite) android.R.drawable.btn_star_big_on
                else android.R.drawable.btn_star_big_off
            )

            binding.root.setOnClickListener { onClick(p) }
            binding.ivStar.setOnClickListener { onStarClick(p) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Pokemon>() {
        override fun areItemsTheSame(oldItem: Pokemon, newItem: Pokemon) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Pokemon, newItem: Pokemon) = oldItem == newItem
    }
}