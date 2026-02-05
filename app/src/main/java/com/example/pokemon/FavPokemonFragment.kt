package com.example.pokemon

import PokemonAdapter
import SharedViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokemon.databinding.FragmentFavPokemonBinding

class FavPokemonFragment : Fragment() {

    private var _binding: FragmentFavPokemonBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SharedViewModel by activityViewModels()
    private lateinit var adapter: PokemonAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavPokemonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // LayoutManager
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())

        // Adapter
        adapter = PokemonAdapter(
            onClick = { pokemon ->
                // Restaurar Pokémon al hacer click
                viewModel.restoreFromTrash(pokemon)
            },
            onStarClick = { /* Puedes dejar vacío */ }
        )
        binding.recycler.adapter = adapter

        // Observamos solo los Pokémon eliminados
        viewModel.getDeletedPokemons().observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }

        // Swipe → borrar permanentemente
        ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: androidx.recyclerview.widget.RecyclerView,
                    viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
                    target: androidx.recyclerview.widget.RecyclerView.ViewHolder
                ): Boolean = false

                override fun onSwiped(
                    viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
                    direction: Int
                ) {
                    val position = viewHolder.bindingAdapterPosition
                    val pokemon = adapter.currentList[position]
                    viewModel.delete(pokemon) // borra permanentemente
                }
            }
        ).attachToRecyclerView(binding.recycler)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
