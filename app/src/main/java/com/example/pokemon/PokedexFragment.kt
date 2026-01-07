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
import androidx.recyclerview.widget.RecyclerView
import com.example.pokemon.databinding.FragmentPokedexBinding

class PokedexFragment : Fragment() {

    private var _binding: FragmentPokedexBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SharedViewModel by activityViewModels()
    private lateinit var adapter: PokemonAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPokedexBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.recycler.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(requireContext())

        adapter = PokemonAdapter(
            onClick = { pokemon ->
                viewModel.select(pokemon)
                findNavController().navigate(
                    R.id.action_pokedexFragment_to_detailsFragment
                )
            },
            onStarClick = { pokemon ->
                viewModel.setFavorite(pokemon.id, !pokemon.isFavorite)
            }
        )

        binding.recycler.adapter = adapter

        viewModel.pokemons.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }

        val itemTouchHelper = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ) = false

                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) {
                    val position = viewHolder.bindingAdapterPosition
                    val pokemon = adapter.currentList[position]
                    viewModel.delete(pokemon)
                }
            }
        )
        itemTouchHelper.attachToRecyclerView(binding.recycler)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
