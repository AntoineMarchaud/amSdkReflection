package com.example.amsdkreflection.sdk

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.example.amsdkreflection.sdk.CsAnalytics.Companion.LOG_FILE_NAME
import java.io.File
import java.io.FileInputStream
import java.util.*


class CustomReader(var mContext: Context) {

    var mInputStream: FileInputStream? = null
    var mScanner: Scanner? = null

    companion object {
        const val TAG = "CustomReader"
    }

    private fun initReader() {
        try {
            mInputStream = FileInputStream(File(mContext.filesDir, LOG_FILE_NAME))
            mScanner = Scanner(mInputStream)
        } catch (e: Exception) {
            mInputStream = null
            mScanner = null
            Log.d(TAG, "Init exception : $e")
        }
    }

    /**
     * Use Scanner to stream our log file
     */
    fun notifyChanged() {

        if (mInputStream == null && mScanner == null) {
            initReader()
        }

        mScanner?.let {
            if (it.hasNextLine()) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(mContext, "$TAG : ${it.nextLine()}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun getNewLine(): String? {
        if (mInputStream == null && mScanner == null) {
            initReader()
        }

        return mScanner?.let {
            if (it.hasNextLine())
                it.nextLine()
            else
                null
        }
    }

    fun clear() {
        mInputStream?.close()
        mScanner?.close()

        mInputStream = null
        mScanner = null
    }
}