package com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack

import androidx.recyclerview.widget.DiffUtil
import com.example.nafis.nf2024.organizeradminpanel.Model.DoubtModel

class DiffDoubtCallBack: DiffUtil.ItemCallback<DoubtModel>() {
    override fun areItemsTheSame(oldItem: DoubtModel, newItem: DoubtModel): Boolean {
         return oldItem.doubtId==newItem.doubtId
    }

    override fun areContentsTheSame(oldItem: DoubtModel, newItem: DoubtModel): Boolean {
        return oldItem.userUid==newItem.userUid &&
                   oldItem.userEmail==newItem.userEmail &&
                   oldItem.userName==newItem.userName &&
                   oldItem.userImgUrl==newItem.userImgUrl &&
                   oldItem.studQuesDesc==newItem.studQuesDesc &&
                   oldItem.teachAnsDesc==newItem.teachAnsDesc  &&
                   oldItem.studQuesImgUrl==newItem.studQuesImgUrl &&
                   oldItem.timestamp==newItem.timestamp &&
                   oldItem.studQuesTitle==newItem.studQuesTitle &&
                   oldItem.teachAnsImgUrl==newItem.teachAnsImgUrl

    }

}