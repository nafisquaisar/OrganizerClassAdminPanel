package com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack

import com.example.nafis.nf2024.organizeradminpanel.Model.Chapter
import com.example.nafis.nf2024.organizeradminpanel.Model.CourseModel

interface ChapterCallback {
    fun onChapterClick(item:Chapter)
    fun onUpdateDoubt(item: Chapter)
    fun deleteCourseChapter(chapterId: String)
}