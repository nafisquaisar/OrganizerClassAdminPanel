package com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack

import androidx.recyclerview.widget.DiffUtil
import com.example.nafis.nf2024.organizeradminpanel.Model.Chapter

class DiffChapterCallBack: DiffUtil.ItemCallback<Chapter>() {
    override fun areItemsTheSame(oldItem: Chapter, newItem: Chapter): Boolean {
           return oldItem.chapId==newItem.chapId
    }

    override fun areContentsTheSame(oldItem: Chapter, newItem: Chapter): Boolean {
          return oldItem.chapName==newItem.chapName
    }
}