package com.example.amsdkreflection.sdk

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.amsdkreflection.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KProperty1


class CsAnalytics(private val mContext: Context) {

    companion object {
        const val TAG = "CsAnalytics"
        const val LOG_FILE_NAME = "logs.txt"
        const val DELAY = 2000L
    }


    private var mReader: CustomReader = CustomReader(mContext)

    // for example :
    // savedPets[DOGS] = "1, 10 ..."
    // savedPets[CATS] = "2, 11..."

    private val mSavedPets = ConcurrentHashMap<String, HashSet<Int>>()


    // custom Debouncer WITHOUT library
    private var mLastTimeClick = System.currentTimeMillis()
    private var mJobDebounce: Job? = null
    private fun launchDebounce(message: String) = GlobalScope.launch {
        delay(DELAY)

        // Log in logcat
        Log.d(TAG, message)

        // write to a file
        writeToFile(message)

        // notify Reader class
        // example 1 : strong link
        mReader.notifyChanged()

        // example 2 : by Broadcasting event
        Intent().also { intent ->
            intent.action = CustomReaderBroadcastReceiver.ACTION_NEW_LINE
            mContext.sendBroadcast(intent)
        }
    }

    private fun writeToFile(message: String) {
        try {
            val myFile = File(mContext.filesDir, LOG_FILE_NAME)
            val writer = FileWriter(myFile, true)
            writer.append("$message\r\n")
            writer.flush()
            writer.close()
        } catch (e: IOException) {
            Log.e(TAG, "File write failed: $e")
        }
    }


    fun trigger(holder: RecyclerView.ViewHolder, position: Int) {

        if (position < 0) return

        //Displays the pop-up (Toast) with the dog or cat count.
        mSavedPets.entries.firstOrNull { list ->
            list.value.contains(position)
        }?.let { oneEntry ->
            val numberToDisplay = oneEntry.value.count {
                it <= position
            }
            val toDisplay = mContext.getString(R.string.ToastText, position, numberToDisplay, oneEntry.key)

            // uncomment if you want a toast
            //Toast.makeText(mContext, toDisplay, Toast.LENGTH_SHORT).show()

            // implement Debounce with Native Kotlin
            // in Java, use Handler.PostDelayed(runnable, 2000) and hander.removecallbacks(runnable)
            if (System.currentTimeMillis() - mLastTimeClick < DELAY) {
                mJobDebounce?.cancel()
            }

            // coroutine : not blocking
            mJobDebounce = launchDebounce(toDisplay)
        }

        mLastTimeClick = System.currentTimeMillis()
    }

    fun track(holder: RecyclerView.ViewHolder, position: Int) {

        //Registers all the pets from each grid.
        if (position < 0) return


        /*
        // first method : very quick
        // no reference to any value of the Model / MyAdapter BUT you need to modify "MyAdapter" and it is forbidden by the test
        val animalName = when (holder.itemViewType) {
            0 -> {
                "cat"
            }
            else -> {
                "dog"
            }
        }*/

        // second method : by reflection. Slower, so use coroutines
        // it does not modify any of the Model/Adapter code,
        // but if someone modify the code (like add a layout around the imageView), it will not work anymore
        GlobalScope.launch {

            try {
                val srcByReflection = readInstanceProperty<ImageView>(holder, "itemView")
                val animalName = srcByReflection.contentDescription.toString()
                synchronized(mSavedPets) {
                    if (mSavedPets.containsKey(animalName)) {
                        mSavedPets[animalName]?.add(position)
                    } else {
                        mSavedPets[animalName] = hashSetOf(position)
                    }
                }
            } catch (e: ClassCastException) {
                e.printStackTrace()
            }
        }
    }

    fun clear() {

        // just clear file
        try {
            PrintWriter(File(mContext.filesDir, LOG_FILE_NAME)).close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        /*
        // or delete file
        val fDelete = File(mContext.filesDir, LOG_FILE_NAME)
        if (fDelete.exists()) {
            if (fDelete.delete()) {
                Log.d(TAG, "file Deleted :" + fDelete.path)
            } else {
                Log.d(TAG, "file not Deleted :" + fDelete.path)
            }
        }*/

        mReader.clear()

        Intent().also { intent ->
            intent.action = CustomReaderBroadcastReceiver.ACTION_CLEAN_FILE
            mContext.sendBroadcast(intent)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <R> readInstanceProperty(instance: Any, propertyName: String): R {
        val property = instance::class.members.first { it.name == propertyName } as KProperty1<Any, *>
        return property.get(instance) as R
    }
}