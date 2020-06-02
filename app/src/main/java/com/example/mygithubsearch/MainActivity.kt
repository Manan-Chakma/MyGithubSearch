package com.example.mygithubsearch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import com.example.mygithubsearch.api.searchRepos
import com.example.mygithubsearch.constants.MyConstants.Companion.MY_TAG
import com.example.mygithubsearch.ui.MainFragment
import com.example.mygithubsearch.ui.SearchRepositoriesViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var viewmodel: SearchRepositoriesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!::viewmodel.isInitialized) {
            viewmodel = ViewModelProvider(this, Injection.provideViewModelFactory(this))
                .get(SearchRepositoriesViewModel::class.java)
        }
        supportFragmentManager.beginTransaction()
            .add(R.id.main_fragmentContainer, MainFragment()).commit()

        Log.d(MY_TAG, "Main Activity: $viewmodel")

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val searchMenuItem = menu.findItem(R.id.action_search)
        val searchView = searchMenuItem.actionView as SearchView
        searchView.imeOptions = EditorInfo.IME_ACTION_DONE

        searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    viewmodel.mQuery.value = query
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }

            }
        )
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        viewmodel.deleteAll()
    }

}
