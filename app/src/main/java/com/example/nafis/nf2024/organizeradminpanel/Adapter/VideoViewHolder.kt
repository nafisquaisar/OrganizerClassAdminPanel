package com.example.nafis.nf2024.organizeradminpanel.Adapter

import android.content.Context
import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack.ChapterCallback
import com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack.VideoCallback
import com.example.nafis.nf2024.organizeradminpanel.Model.Chapter
import com.example.nafis.nf2024.organizeradminpanel.Model.Note
import com.example.nafis.nf2024.organizeradminpanel.Model.Video
import com.example.nafis.nf2024.organizeradminpanel.R
import com.example.nafis.nf2024.organizeradminpanel.databinding.SubjectItemBinding
import com.example.nafis.nf2024.organizeradminpanel.databinding.VideoItemBinding

class VideoViewHolder(
    var binding:VideoItemBinding, var context: Context
    , var callback: VideoCallback
):RecyclerView.ViewHolder(binding.root) {

    fun bind(item:Video){
        binding.apply {
            videoTitle.text=item.videoTitle
            videoDate.text=item.date
            videoTime.text=item.time


        }

        itemView.setOnClickListener {
            callback.onVideoClick(item)
        }

        binding.moreOptionVideo.setOnClickListener {
            popMenuOptionOpen(item,it)
        }
    }

    private fun popMenuOptionOpen(item: Video, it: View?) {
        val popUpMenu = PopupMenu(context, it)
        popUpMenu.inflate(R.menu.courseitem_more_option)
        popUpMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.pop_update -> {
                    callback.updateVideoClick(item)
                    true
                }

                R.id.pop_delete -> {
                    callback.deleteVideoClick(item.videoId)
                    true
                }

                else -> false
            }
        }
        popUpMenu.show()
    }

}
