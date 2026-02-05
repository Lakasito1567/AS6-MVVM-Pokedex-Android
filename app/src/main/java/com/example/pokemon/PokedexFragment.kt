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

        binding.recycler.layoutManager = LinearLayoutManager(requireContext())

        viewModel.fetchPokemons()

        adapter = PokemonAdapter(
            onClick = { pokemon ->
                viewModel.select(pokemon)
                findNavController().navigate(
                    R.id.action_pokedexFragment_to_detailsFragment
                )
            },
            onStarClick = {}
        )

        binding.recycler.adapter = adapter

        viewModel.getFilteredActivePokemons().observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }

        binding.recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisible = layoutManager.findLastVisibleItemPosition()

                // Si estamos cerca del final, cargamos más
                if (!viewModel.isLoading && lastVisible >= totalItemCount - 5) {
                    viewModel.fetchPokemons()
                }
            }
        })


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
                    viewModel.moveToTrash(pokemon) // marca isDeleted = true
                }
            }
        ).attachToRecyclerView(binding.recycler)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
