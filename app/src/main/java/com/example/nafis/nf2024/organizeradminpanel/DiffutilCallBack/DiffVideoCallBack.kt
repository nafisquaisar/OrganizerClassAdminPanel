package com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack

import androidx.recyclerview.widget.DiffUtil
import com.example.nafis.nf2024.organizeradminpanel.Model.Video

class DiffVideoCallBack : DiffUtil.ItemCallback<Video>() {
    override fun areItemsTheSame(oldItem: Video, newItem: Video): Boolean {
        return oldItem.videoId == newItem.videoId
    }

    override fun areContentsTheSame(oldItem: Video, newItem: Video): Boolean {
        return oldItem.videoTitle == newItem.videoTitle &&
                oldItem.videoUrl == newItem.videoUrl &&
                oldItem.date == newItem.date &&
                oldItem.time == newItem.time &&
                oldItem.des == newItem.des

    }
}