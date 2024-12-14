package com.example.nafis.nf2024.organizeradminpanel.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack.DiffDoubtUserCallBack
import com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack.DoubtUserCallBack
import com.example.nafis.nf2024.organizeradminpanel.Model.UserProfile
import com.example.nafis.nf2024.organizeradminpanel.databinding.UserItemViewBinding

class DoubtUserAdapter(val context: Context,val callBack: DoubtUserCallBack): ListAdapter<UserProfile, DoubtUserViewHolder>(DiffDoubtUserCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoubtUserViewHolder {
            val view=UserItemViewBinding.inflate(LayoutInflater.from(context),parent,false)
            return DoubtUserViewHolder(context,view,callBack)
    }

    override fun onBindViewHolder(holder: DoubtUserViewHolder, position: Int) {
                     val currItem=getItem(position)
                     holder.bind(currItem)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}