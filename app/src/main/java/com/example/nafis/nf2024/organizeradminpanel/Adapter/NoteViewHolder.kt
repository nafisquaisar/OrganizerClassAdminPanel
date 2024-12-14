package com.example.nafis.nf2024.organizeradminpanel.Adapter

import android.content.Context
import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack.ChapterCallback
import com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack.NoteCallback
import com.example.nafis.nf2024.organizeradminpanel.Model.Chapter
import com.example.nafis.nf2024.organizeradminpanel.Model.Note
import com.example.nafis.nf2024.organizeradminpanel.R
import com.example.nafis.nf2024.organizeradminpanel.databinding.NoteItemBinding
import com.example.nafis.nf2024.organizeradminpanel.databinding.SubjectItemBinding

class NoteViewHolder(
    var binding: NoteItemBinding, var context: Context, var callback: NoteCallback
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Note) {
        binding.apply {
            NoteName.text = item.noteTitle
            Notedate.text = item.date
        }

        itemView.setOnClickListener {
            callback.onNoteClick(item)
        }

        binding.noteMoreOption.setOnClickListener {
            popMenuOptionOpen(item, it)
        }

    }

    private fun popMenuOptionOpen(item: Note, it: View?) {
        val popUpMenu = PopupMenu(context, it)
        popUpMenu.inflate(R.menu.courseitem_more_option)
        popUpMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.pop_update -> {
                    callback.updateNoteClick(item)
                    true
                }

                R.id.pop_delete -> {
                    callback.deleteNoteClick(item.noteId)
                    true
                }

                else -> false
            }
        }
        popUpMenu.show()
    }
}
