package com.example.amsdkreflection.sdk

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import java.io.File
import java.io.FileInputStream
import java.util.*

class CustomReaderBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "CustomReaderBroadcastReceiver"
        const val ACTION_NEW_LINE = "com.action.ACTION_NEW_LINE"
        const val ACTION_CLEAN_FILE = "com.action.ACTION_CLEAN_FILE"

        private var mInputStream: FileInputStream? = null
        private var mScanner: Scanner? = null
    }


    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action.equals(ACTION_NEW_LINE)) {

            if (mInputStream == null && mScanner == null) {
                initReader(context)
            }

            mScanner?.let {
                if (it.hasNextLine())
                    Toast.makeText(context, "$TAG : ${it.nextLine()}", Toast.LENGTH_SHORT).show()
            }

        } else if (intent?.action.equals(ACTION_CLEAN_FILE)) {
            mInputStream?.close()
            mInputStream = null

            mScanner?.close()
            mScanner = null
        }
    }


    private fun initReader(ctx: Context) {
        try {
            mInputStream = FileInputStream(File(ctx.filesDir, CsAnalytics.LOG_FILE_NAME))
            mScanner = Scanner(mInputStream)
        } catch (e: Exception) {
            mInputStream = null
            mScanner = null
            Log.d(CustomReader.TAG, "Init exception : $e")
        }
    }
}