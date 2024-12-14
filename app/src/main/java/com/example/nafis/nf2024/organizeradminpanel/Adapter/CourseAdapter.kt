package com.example.nafis.nf2024.organizeradminpanel.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack.CouseCallback
import com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack.DiffCourseCallBack
import com.example.nafis.nf2024.organizeradminpanel.Model.CourseModel
import com.example.nafis.nf2024.organizeradminpanel.databinding.CourseItemBinding

class CourseAdapter(var context: Context,var callback: CouseCallback,val courseList:ArrayList<CourseModel>):
    ListAdapter<CourseModel, CourseViewHolder>(DiffCourseCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
          val binding=CourseItemBinding.inflate(LayoutInflater.from(context),parent,false)
          return CourseViewHolder(binding,context,callback)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
           var item=courseList[position]
           holder.bind(item)
    }
}