package com.example.mygithubsearch.data

import android.util.Log
import androidx.paging.DataSource
import com.example.mygithubsearch.constants.MyConstants.Companion.MY_TAG
import com.example.mygithubsearch.db.RepoDao
import com.example.mygithubsearch.model.Repo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executor

class GithubLocalCache(
    private val repoDao: RepoDao
) {

    fun insert(repos: List<Repo>): Boolean {
        Log.d(MY_TAG, "GithubLocalCache: inserting ${repos.size}")
        return try {
            CoroutineScope(IO).launch {
                Log.d(MY_TAG, "GithubLocalCache: inserting ${repos.size} repos")
                withContext(Dispatchers.Default) {
                    repoDao.insert(repos)
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun reposByName(name: String): DataSource.Factory<Int, Repo> {
        // appending '%' so we can allow other characters to be before and after the query string
        Log.d(MY_TAG, "GithubLocalCache: repos by name")
        val query = "%${name.replace(' ', '%')}%"
        return repoDao.reposByName(query)
    }

    fun deleteAll() {
        try {
            CoroutineScope(IO).launch {
                withContext(Dispatchers.Default) {
                    repoDao.deleteAll()
                }
            }
            Log.d(MY_TAG, "Successfully deleted")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(MY_TAG, "Failed to deleted")
        }
    }
}