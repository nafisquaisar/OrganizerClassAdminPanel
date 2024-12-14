package com.example.nafis.nf2024.organizeradminpanel.Model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class CourseModel(
    var courseId: String = "",
    var courseName: String = "",
    var courseDesc: String = "",
    var courseAmount: String = "",
    var offerAmount: String = "",
    var courseImgUrl: String = "",
    var isCourseDisable:Boolean=false,
    @ServerTimestamp var courseDate: Date? = null, // Use Date or Timestamp here
    var subjects: List<Subject> = listOf()
)

data class Subject(
    val subjectId: String = "",
    val subjectName: String = "",
    val chapters: List<Chapter> = listOf()
)

data class Chapter(
    val chapId: String = "",
    val chapName: String = "",
    val resources: Resources = Resources()
)

data class Resources(
    val videos: List<Video> = listOf(),
    val notes: List<Note> = listOf(),
    val dpps: List<Dpp> = listOf()
)

data class Video(
    val videoId: String = "",
    val videoTitle: String = "",
    val videoUrl: String = "",
    val des:String="",
    var date:String="",
    var time:String=""
)

data class Note(
    val noteId: String = "",
    val noteTitle: String = "",
    val noteUrl: String = "",
    var date:String?=null
)

data class Dpp(
    val dppId: String = "",
    val dppTitle: String = "",
    val dppUrl: String = ""
)
