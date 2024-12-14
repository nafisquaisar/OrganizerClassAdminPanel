package com.example.nafis.nf2024.organizeradminpanel.Adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack.DoubtListCallBack
import com.example.nafis.nf2024.organizeradminpanel.Model.DoubtModel
import com.example.nafis.nf2024.organizeradminpanel.R
import com.example.nafis.nf2024.organizeradminpanel.databinding.DoubtItemBinding

class DoubtListViewAdapter(val binding:DoubtItemBinding,val callBack: DoubtListCallBack,
        ):RecyclerView.ViewHolder(binding.root) {
        fun bind(item:DoubtModel){
            binding.doubtPosition.text="${(position+1)} )"
            binding.doubtTitle.text=item.studQuesTitle

                itemView.setOnClickListener {
                        callBack.DoubtListClick(item,position)
                }
            when {
                item.status == null -> {
                    // New doubt - Green icon
                    binding.stutus.setImageResource(R.drawable.status_green) // Green for new
                    binding.stutus.visibility = View.VISIBLE

                }
                item.status == true -> {
                    // Viewed doubt - Yellow icon
                    binding.stutus.setImageResource(R.drawable.status_yellow) // Yellow for viewed
                    binding.stutus.visibility = View.VISIBLE
                }
                else -> {
                    // No status - Hide the icon
                    binding.stutus.visibility = View.GONE
                }
            }

        }
}
