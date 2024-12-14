package com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack

import com.example.nafis.nf2024.organizeradminpanel.Model.CourseModel

interface CouseCallback {
    fun onCourseClick(item: CourseModel)
    fun onCourseUpdate(item: CourseModel)
    fun onCourseDelete(itemId: String)
    fun onCourseDisable(itemId: String)
}