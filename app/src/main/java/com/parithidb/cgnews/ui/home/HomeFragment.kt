package com.parithidb.cgnews.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.parithidb.cgnews.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment: Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: NewsViewModel by viewModels()
    private lateinit var adapter: ArticlesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        observeData()
        setupSwipeRefresh()

    }

    private fun setupAdapter() {
        adapter = ArticlesAdapter { url ->
            val intent = Intent(requireContext(), WebActivity::class.java)
            intent.putExtra("url", url)
            startActivity(intent)
        }
        binding.viewPager.adapter = adapter

        // Stop shimmer when data loads
        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates ->
                val isLoading = loadStates.refresh is LoadState.Loading

                if (isLoading) {
                    binding.shimmerLayout.startShimmer()
                } else {
                    binding.shimmerLayout.stopShimmer()
                }

                binding.shimmerLayout.isVisible = isLoading
                binding.viewPager.isVisible = !isLoading
                binding.swipeRefreshLayout.isRefreshing = isLoading
            }
        }
    }

    private fun observeData() {
        lifecycleScope.launchWhenStarted {
            viewModel.topHeadlines.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            adapter.refresh() // Paging refresh
        }
    }
}