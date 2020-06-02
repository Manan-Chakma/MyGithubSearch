package com.example.mygithubsearch.data

import android.util.Log
import androidx.paging.LivePagedListBuilder
import com.example.mygithubsearch.api.GithubService
import com.example.mygithubsearch.constants.MyConstants.Companion.MY_TAG
import com.example.mygithubsearch.model.RepoSearchResult

class GithubRepository(
    private val service: GithubService,
    private val cache: GithubLocalCache // local cache
) {

    fun search(query: String): RepoSearchResult {
        Log.d(MY_TAG, "GithubRepository New query: $query")

        val dataSourceFactory = cache.reposByName(query)


        val boundaryCallback = RepoBoundaryCallback(query, service, cache) // boundary callback
        val networkErrors = boundaryCallback.networkErrors

        // Get the paged list
        val data = LivePagedListBuilder(dataSourceFactory, DATABASE_PAGE_SIZE)
            .setBoundaryCallback(boundaryCallback)
            .build()

        // Get the network errors exposed by the boundary callback
        return RepoSearchResult(data, networkErrors)
    }

    fun deleteAll() {
        cache.deleteAll()
    }

    companion object {
        private const val DATABASE_PAGE_SIZE = 20
    }
}