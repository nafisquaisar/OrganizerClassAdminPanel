package com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack

import androidx.recyclerview.widget.DiffUtil
import com.example.nafis.nf2024.organizeradminpanel.Model.CourseModel

class DiffCourseCallBack: DiffUtil.ItemCallback<CourseModel>() {
    override fun areItemsTheSame(oldItem: CourseModel, newItem: CourseModel): Boolean {
           return oldItem.courseId==newItem.courseId
    }

    override fun areContentsTheSame(oldItem: CourseModel, newItem: CourseModel): Boolean {
          return oldItem.courseName==newItem.courseName&&
                       oldItem.courseDesc==newItem.courseDesc &&
                       oldItem.courseAmount==newItem.courseAmount &&
                       oldItem.courseDate==newItem.courseDate &&
                       oldItem.courseImgUrl==newItem.courseImgUrl
    }
}