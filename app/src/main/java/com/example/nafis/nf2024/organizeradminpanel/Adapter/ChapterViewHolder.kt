package com.example.nafis.nf2024.organizeradminpanel.Adapter

import android.content.Context
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack.ChapterCallback
import com.example.nafis.nf2024.organizeradminpanel.Model.Chapter
import com.example.nafis.nf2024.organizeradminpanel.R
import com.example.nafis.nf2024.organizeradminpanel.databinding.SubjectItemBinding

class ChapterViewHolder(
    var binding:SubjectItemBinding, var context: Context
    , var callback: ChapterCallback
):RecyclerView.ViewHolder(binding.root) {

    fun bind(item:Chapter){
        binding.apply {
            CourseSubName.text=item.chapName
            when(item.chapName){
                "Science"->{
                    subjectIcon.setImageResource(R.drawable.sciencelogo)
                }
                "Chemistry"->{
                    subjectIcon.setImageResource(R.drawable.chemistrysubjectlogo)
                }
                else->{
                    subjectIcon.setImageResource(R.drawable.course)
                }
            }
        }

        itemView.setOnClickListener {
            callback.onChapterClick(item)
        }

            getOption(item)
    }

    private fun getOption(item: Chapter) {
        binding.moreOptionSubject.setOnClickListener {
            val popupMenu=PopupMenu(context,itemView)
            popupMenu.inflate(R.menu.courseitem_more_option)

            popupMenu.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.pop_update->{
                        callback.onUpdateDoubt(item)
                        true
                    }
                    R.id.pop_delete->{
                        item.chapId.let { it1 -> deleteDoubt(it1) }
                        true
                    }
                    else-> false
                }
            }
            popupMenu.show()
        }

    }

    private fun deleteDoubt(doubtId: String) {
        callback.deleteCourseChapter(doubtId)
    }
}
