package com.example.nafis.nf2024.organizeradminpanel.Adapter

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack.DoubtUserCallBack
import com.example.nafis.nf2024.organizeradminpanel.Model.DoubtModel
import com.example.nafis.nf2024.organizeradminpanel.Model.UserProfile
import com.example.nafis.nf2024.organizeradminpanel.R
import com.example.nafis.nf2024.organizeradminpanel.databinding.UserItemViewBinding

class DoubtUserViewHolder(val context: Context, val binding:UserItemViewBinding
                          , val callBack: DoubtUserCallBack
):RecyclerView.ViewHolder(binding.root) {
        fun bind(item:UserProfile){
            Glide.with(context)
                .load(item.UserUrl)
                .placeholder(R.drawable.user_profile)
                .into(binding.userProfile)
            binding.userName.text=item.UserName
            binding.userEmail.text=item.UserEmail
            itemView.setOnClickListener {
                  callBack.DoubtUserClick(item)
            }
          binding.apply {
              // Set status icon visibility and color
              when (item.status) {
                  "yellow" -> {
                      status.visibility = View.VISIBLE
                      status.setImageResource(R.drawable.status_yellow)
                  }
                  "green" -> {
                      status.visibility = View.VISIBLE
                      status.setImageResource(R.drawable.status_green)
                  }
                  "hidden" -> {
                      status.visibility = View.GONE
                  }
              }
          }
            
        }
}
