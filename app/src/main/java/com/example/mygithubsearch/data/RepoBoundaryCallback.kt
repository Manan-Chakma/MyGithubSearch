package com.example.mygithubsearch.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.example.mygithubsearch.api.GithubService
import com.example.mygithubsearch.api.searchRepos
import com.example.mygithubsearch.constants.MyConstants.Companion.MY_TAG
import com.example.mygithubsearch.model.Repo

class RepoBoundaryCallback(
    private val query: String,
    private val service: GithubService,
    private val cache: GithubLocalCache
) : PagedList.BoundaryCallback<Repo>() {

    companion object {
        private const val NETWORK_PAGE_SIZE = 50
    }


    private var lastRequestedPage = 1

    private val _networkErrors = MutableLiveData<String>()
    val networkErrors: LiveData<String>
        get() = _networkErrors

    private var isRequestInProgress = false

    /**
     * Database returned 0 items. We should query the backend for more items.
     */
    override fun onZeroItemsLoaded() {
        Log.d(MY_TAG, "RepoBoundaryCallback onZeroItemsLoaded")
        requestAndSaveData(query)
    }

    /**
     * When all items in the database were loaded, we need to query the backend for more items.
     */
    override fun onItemAtEndLoaded(itemAtEnd: Repo) {
        Log.d(MY_TAG, "RepoBoundaryCallback onItemAtEndLoaded")
        requestAndSaveData(query)
    }

    private fun requestAndSaveData(query: String) {
        try {
            if (isRequestInProgress) return

            isRequestInProgress = true
            searchRepos(service, query, lastRequestedPage, NETWORK_PAGE_SIZE, { repos ->
/*                cache.insert(repos) {
                    lastRequestedPage++
                    isRequestInProgress = false
                }*/
                if (cache.insert(repos)) {
                    lastRequestedPage++
                    isRequestInProgress = false
                } else {
                    _networkErrors.postValue("Can't insert data in cache")
                }
            }, { error ->
                _networkErrors.postValue(error)
                isRequestInProgress = false
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}