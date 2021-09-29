package com.devshish.internship.presentation.ui.search

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.devshish.internship.R
import com.devshish.internship.databinding.FragmentSearchBinding
import com.devshish.internship.presentation.ui.utils.onQueryTextChanged
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment(R.layout.fragment_search) {

    private val binding by viewBinding(FragmentSearchBinding::bind)
    private val viewModel: SearchViewModel by viewModel()
    private val searchSongAdapter = ItemSearchSongAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            rvSongs.apply {
                adapter = searchSongAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }

            topAppBar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.actionSearch -> {
                        val searchView = menuItem.actionView as SearchView
                        searchView.onQueryTextChanged {
                            tvDescription.visibility = View.GONE
                            viewModel.searchSongs(it)
                        }
                        true
                    }
                    else -> false
                }
            }
        }

        with(viewModel) {
            searching.observe(viewLifecycleOwner) {
                searchSongAdapter.submitList(it)
            }

            isLoading.observe(viewLifecycleOwner) { isLoading ->
                if (isLoading) {
                    binding.progressIndicator.show()
                } else {
                    binding.progressIndicator.hide()
                }
            }
        }
    }
}
