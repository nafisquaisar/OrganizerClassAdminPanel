package com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack

import androidx.recyclerview.widget.DiffUtil
import com.example.nafis.nf2024.organizeradminpanel.Model.Note

class DiffNoteCallBack: DiffUtil.ItemCallback<Note>() {
    override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
           return oldItem.noteId==newItem.noteId
    }

    override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
          return oldItem.noteTitle==newItem.noteTitle &&
                  oldItem.noteUrl==newItem.noteUrl &&
                  oldItem.date==newItem.date

                     
    }
}