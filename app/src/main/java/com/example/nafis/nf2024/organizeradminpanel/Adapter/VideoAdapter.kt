package com.example.nafis.nf2024.organizeradminpanel.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack.ChapterCallback
import com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack.CouseCallback
import com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack.DiffChapterCallBack
import com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack.DiffCourseCallBack
import com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack.DiffVideoCallBack
import com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack.VideoCallback
import com.example.nafis.nf2024.organizeradminpanel.Model.Chapter
import com.example.nafis.nf2024.organizeradminpanel.Model.CourseModel
import com.example.nafis.nf2024.organizeradminpanel.Model.Video
import com.example.nafis.nf2024.organizeradminpanel.databinding.CourseItemBinding
import com.example.nafis.nf2024.organizeradminpanel.databinding.SubjectItemBinding
import com.example.nafis.nf2024.organizeradminpanel.databinding.VideoItemBinding

class VideoAdapter(var context: Context, var callback: VideoCallback, val courseList:ArrayList<Video>):
    ListAdapter<Video, VideoViewHolder>(DiffVideoCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
          val binding=VideoItemBinding.inflate(LayoutInflater.from(context),parent,false)
          return VideoViewHolder(binding,context,callback)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
           var item=courseList[position]
           holder.bind(item)
    }
}