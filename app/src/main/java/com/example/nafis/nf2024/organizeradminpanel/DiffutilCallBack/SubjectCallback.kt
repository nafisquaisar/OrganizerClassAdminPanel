package com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack

import com.example.nafis.nf2024.organizeradminpanel.Model.CourseModel
import com.example.nafis.nf2024.organizeradminpanel.Model.Subject

interface SubjectCallback {
    fun onSubjectClick(item:Subject)
    fun subjectDelete(itemId:String)
    fun subjectUpdate(item:Subject)
}