package com.example.amsdkreflection

import android.content.Context
import java.util.*

class Model(private val mContext: Context) {

    companion object {
        const val MAX = 200

        @JvmStatic
        var CAT = "cat"

        @JvmStatic
        var DOG = "dog"
    }

    private var mPetList: MutableList<String> = mutableListOf()

    fun createPetList(): List<String> {
        for (i in 0..MAX) {
            mPetList.add(randomFill())
        }
        return mPetList
    }

    private fun randomFill(): String {
        val random = Random()
        val isOne = random.nextBoolean()
        return if (isOne) CAT else DOG
    }
}