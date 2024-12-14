package com.example.nafis.nf2024.organizeradminpanel.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack.ChapterCallback
import com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack.CouseCallback
import com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack.DiffChapterCallBack
import com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack.DiffCourseCallBack
import com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack.DiffNoteCallBack
import com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack.NoteCallback
import com.example.nafis.nf2024.organizeradminpanel.Model.Chapter
import com.example.nafis.nf2024.organizeradminpanel.Model.CourseModel
import com.example.nafis.nf2024.organizeradminpanel.Model.Note
import com.example.nafis.nf2024.organizeradminpanel.databinding.CourseItemBinding
import com.example.nafis.nf2024.organizeradminpanel.databinding.NoteItemBinding
import com.example.nafis.nf2024.organizeradminpanel.databinding.SubjectItemBinding

class NoteAdapter(var context: Context, var callback: NoteCallback, val courseList:ArrayList<Note>):
    ListAdapter<Note, NoteViewHolder>(DiffNoteCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
          val binding=NoteItemBinding.inflate(LayoutInflater.from(context),parent,false)
          return NoteViewHolder(binding,context,callback)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
           var item=courseList[position]
           holder.bind(item)
    }
}