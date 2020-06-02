package com.example.mygithubsearch.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mygithubsearch.Injection

import com.example.mygithubsearch.R
import com.example.mygithubsearch.constants.MyConstants
import com.example.mygithubsearch.constants.MyConstants.Companion.MY_TAG
import com.example.mygithubsearch.model.Repo


class MainFragment : Fragment() {


    private lateinit var viewmodel: SearchRepositoriesViewModel
    private lateinit var recyclerView: RecyclerView
    private val adapter = ReposAdapter()
    private lateinit var emptyList: TextView
    private lateinit var cProgress: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_main, container, false)
        emptyList = root.findViewById(R.id.emptyList)
        cProgress = root.findViewById(R.id.progressBar_cycle)

        if (!::viewmodel.isInitialized) {
            viewmodel = ViewModelProvider(
                requireActivity(),
                Injection.provideViewModelFactory(requireContext())
            )
                .get(SearchRepositoriesViewModel::class.java)
        }
        recyclerView = root.findViewById(R.id.recyclerview)
        Log.d(MY_TAG, "Main Fragment: $viewmodel")

        val query = savedInstanceState?.getString(LAST_SEARCH_QUERY) ?: DEFAULT_QUERY
        initAdapter()
        viewmodel.searchRepo(query)

        initSearch()

        return root
    }

    private fun initSearch() {
        viewmodel.mQuery.observe(viewLifecycleOwner, Observer {
            if (it.trim().isNotBlank()) {
                updateRepoFromInput(it)
            }
        })
    }

    private fun updateRepoFromInput(query: String?) {
        recyclerView.scrollToPosition(0)
        if (query != null) {
            viewmodel.searchRepo(query)
            adapter.submitList(null)
        }
    }


    private fun initAdapter() {
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewmodel.repos.observe(viewLifecycleOwner, Observer {
            Log.d(MY_TAG, "list size: ${it?.size}")
            showEmptyList(it?.size == 0)
            adapter.submitList(it)
        })
        viewmodel.networkErrors.observe(viewLifecycleOwner, Observer {
            Log.d(MY_TAG, "Network Error")
            Toast.makeText(context, "\uD83D\uDE28 Wooops $it", Toast.LENGTH_LONG).show()
        })
    }

    private fun showEmptyList(show: Boolean) {
        if (show) {
            cProgress.visibility = View.VISIBLE
            emptyList.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            cProgress.visibility = View.GONE
            emptyList.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(LAST_SEARCH_QUERY, viewmodel.lastQueryValue())
    }

    companion object {
        private const val LAST_SEARCH_QUERY: String = "last_search_query"
        private const val DEFAULT_QUERY = "Android"
    }


}
