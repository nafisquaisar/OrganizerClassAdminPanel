package com.example.nafis.nf2024.organizeradminpanel.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack.DiffCourseCallBack
import com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack.DiffSubjectCallBack
import com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack.SubjectCallback
import com.example.nafis.nf2024.organizeradminpanel.Model.CourseModel
import com.example.nafis.nf2024.organizeradminpanel.Model.Subject
import com.example.nafis.nf2024.organizeradminpanel.databinding.SubjectItemBinding

class SubjectAdapter(var context: Context, var callback: SubjectCallback, val courseList:ArrayList<Subject>):
    ListAdapter<Subject, SubjectViewHolder>(DiffSubjectCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
          val binding=SubjectItemBinding.inflate(LayoutInflater.from(context),parent,false)
          return SubjectViewHolder(binding,context,callback)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
           var item=courseList[position]
           holder.bind(item)
    }
}