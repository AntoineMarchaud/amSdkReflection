package com.example.amsdkreflection

import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.amsdkreflection.databinding.ActivityMainBinding
import com.example.amsdkreflection.sdk.CsAnalytics
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var myCsAnalytics : CsAnalytics
  
    lateinit var myAdapter: MyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        myAdapter = MyAdapter(myCsAnalytics)

        with(binding) {

            with(toolbar) {
                setSupportActionBar(this)
            }

            with(contentLayout.recyclerView) {
                layoutManager = myGridLayoutManager
                adapter = myAdapter
            }
        }

        // survive screen orientation
        viewModel.petsLiveData.observe(this, {
            myAdapter.mPetList = it
            myAdapter.notifyDataSetChanged()
        })
    }


    @get:VisibleForTesting
    val myGridLayoutManager: GridLayoutManager
        get() = GridLayoutManager(this, 2)

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
}