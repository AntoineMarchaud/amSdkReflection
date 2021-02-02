package com.example.amsdkreflection

import android.app.Application
import android.content.BroadcastReceiver
import android.content.IntentFilter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.amsdkreflection.sdk.CustomReaderBroadcastReceiver

class MainActivityViewModel(var app: Application) : AndroidViewModel(app) {

    private var mActivityModel: Model? = null
    val petsLiveData: MutableLiveData<List<String>> = MutableLiveData()
    private val br: BroadcastReceiver = CustomReaderBroadcastReceiver()

    init {
        mActivityModel = Model(app)

        petsLiveData.postValue(mActivityModel?.createPetList())
        val filter = IntentFilter().apply {
            addAction(CustomReaderBroadcastReceiver.ACTION_NEW_LINE)
            addAction(CustomReaderBroadcastReceiver.ACTION_CLEAN_FILE)
        }
        app.registerReceiver(br, filter)
    }


    override fun onCleared() {
        super.onCleared()
        app.unregisterReceiver(br)
    }
}