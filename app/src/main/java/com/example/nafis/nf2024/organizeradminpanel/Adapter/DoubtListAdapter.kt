package com.example.nafis.nf2024.organizeradminpanel.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack.DiffDoubtCallBack
import com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack.DoubtListCallBack
import com.example.nafis.nf2024.organizeradminpanel.Model.DoubtModel
import com.example.nafis.nf2024.organizeradminpanel.databinding.DoubtItemBinding
import java.util.ArrayList

class DoubtListAdapter( val context: Context,
         val callBack: DoubtListCallBack):
    ListAdapter<DoubtModel, DoubtListViewAdapter>(DiffDoubtCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoubtListViewAdapter {
         val view=DoubtItemBinding.inflate(LayoutInflater.from(context),parent,false)
          return DoubtListViewAdapter(view,callBack)
    }

    override fun onBindViewHolder(holder: DoubtListViewAdapter, position: Int) {
           val model=getItem(position)
           holder.bind(model)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}
