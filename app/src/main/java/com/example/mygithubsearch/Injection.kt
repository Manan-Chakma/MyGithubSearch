package com.example.mygithubsearch

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.example.mygithubsearch.api.GithubService
import com.example.mygithubsearch.data.GithubLocalCache
import com.example.mygithubsearch.data.GithubRepository
import com.example.mygithubsearch.db.RepoDatabase
import com.example.mygithubsearch.ui.ViewModelFactory
import java.util.concurrent.Executors

object Injection {
    private fun provideCache(context: Context): GithubLocalCache {
        val database = RepoDatabase.getInstance(context)
        return GithubLocalCache(database.reposDao())
    }


    private fun provideGithubRepository(context: Context): GithubRepository {
        return GithubRepository(GithubService.create(), provideCache(context))
    }


    fun provideViewModelFactory(context: Context): ViewModelProvider.Factory {
        return ViewModelFactory(provideGithubRepository(context))
    }
}