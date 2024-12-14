package com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack

import androidx.recyclerview.widget.DiffUtil
import com.example.nafis.nf2024.organizeradminpanel.Model.UserProfile

class DiffDoubtUserCallBack: DiffUtil.ItemCallback<UserProfile>() {
    override fun areItemsTheSame(oldItem: UserProfile, newItem: UserProfile): Boolean {
         return oldItem.uid==newItem.uid
    }

    override fun areContentsTheSame(oldItem: UserProfile, newItem: UserProfile): Boolean {
        return oldItem.UserName==newItem.UserName &&
                   oldItem.UserEmail==newItem.UserEmail &&
                   oldItem.UserUrl==newItem.UserUrl
    }

}