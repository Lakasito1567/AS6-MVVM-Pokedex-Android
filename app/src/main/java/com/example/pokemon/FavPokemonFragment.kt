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
import androidx.recyclerview.widget.RecyclerView
import com.example.pokemon.databinding.FragmentFavPokemonBinding
import com.example.pokemon.R

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

        binding.recycler.layoutManager = LinearLayoutManager(requireContext())

        adapter = PokemonAdapter(
            onClick = { pokemon ->
                viewModel.select(pokemon)
                findNavController().navigate(
                    R.id.action_favPokemonFragment_to_detailsFragment5
                )
            },
            onStarClick = { pokemon ->
                viewModel.setFavorite(pokemon.id, false)
            }
        )

        binding.recycler.adapter = adapter

        viewModel.favoritePokemons.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }

        val itemTouchHelper = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
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
                    val pos = viewHolder.bindingAdapterPosition
                    val pokemon = adapter.currentList[pos]
                    viewModel.setFavorite(pokemon.id, false)
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