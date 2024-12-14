package com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack

import com.example.nafis.nf2024.organizeradminpanel.Model.CourseModel
import com.example.nafis.nf2024.organizeradminpanel.Model.Note

interface NoteCallback {
    fun onNoteClick(item: Note)
    fun updateNoteClick(item: Note)
    fun deleteNoteClick(itemId: String)
}