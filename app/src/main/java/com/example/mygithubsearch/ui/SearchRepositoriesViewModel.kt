package com.example.mygithubsearch.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.example.mygithubsearch.constants.MyConstants.Companion.MY_TAG
import com.example.mygithubsearch.data.GithubRepository
import com.example.mygithubsearch.model.Repo
import com.example.mygithubsearch.model.RepoSearchResult

class SearchRepositoriesViewModel(private val repository: GithubRepository) : ViewModel() {

    var mQuery =  MutableLiveData<String>()

    private val queryLiveData = MutableLiveData<String>()
    private val repoResult: LiveData<RepoSearchResult> = Transformations.map(queryLiveData) {
        Log.d(MY_TAG, "ViewModel Search Query:")
        repository.search(it)
    }

    val repos: LiveData<PagedList<Repo>> = Transformations.switchMap(repoResult) { it -> it.data }
    val networkErrors: LiveData<String> = Transformations.switchMap(repoResult) { it ->
        it.networkErrors
    }

    fun searchRepo(queryString: String) {
        Log.d(MY_TAG, "ViewModel Search Query: $queryString")
        queryLiveData.postValue(queryString)
    }
    fun lastQueryValue(): String? = queryLiveData.value

    fun deleteAll() {
        repository.deleteAll()
    }
}