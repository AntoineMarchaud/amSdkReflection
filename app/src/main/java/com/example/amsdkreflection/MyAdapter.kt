package com.example.amsdkreflection

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.amsdkreflection.MyAdapter.PetViewHolder
import com.example.amsdkreflection.databinding.ItemLayoutBinding
import com.example.amsdkreflection.sdk.CsAnalytics

// modern ViewBinding
class MyAdapter(private val mCsAnalytics: CsAnalytics) : RecyclerView.Adapter<PetViewHolder>() {

    var mPetList: List<String> = mutableListOf()

    // if we do that, the sdk is created at each new screen orientation, and the datas are not stored
    //private val mCsAnalytics: CsAnalytics = CsAnalytics(mContext)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        mCsAnalytics.clear()
        val binding = ItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PetViewHolder(binding)
    }

    /*
    // first methode:  very quick, but I cant modify the file MyAdapter
    override fun getItemViewType(position: Int): Int {
        super.getItemViewType(position)

        return when (mPetList[position]) {
            Model.CAT -> {
                0
            }
            else -> {
                1
            }
        }
    }*/

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {

        with(holder.binding) {
            when (mPetList[position]) {
                Model.CAT -> {
                    pet.setImageResource(R.drawable.cat)
                    pet.contentDescription = Model.CAT
                }
                else -> {
                    pet.setImageResource(R.drawable.dog)
                    pet.contentDescription = Model.DOG
                }
            }
            pet.setOnClickListener { mCsAnalytics.trigger(holder, position) }
        }

        mCsAnalytics.track(holder, position)
    }

    override fun getItemCount(): Int {
        return mPetList.size
    }

    inner class PetViewHolder(var binding: ItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}