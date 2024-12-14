package com.example.nafis.nf2024.organizeradminpanel.Adapter

import android.content.Context
import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack.SubjectCallback
import com.example.nafis.nf2024.organizeradminpanel.Model.Subject
import com.example.nafis.nf2024.organizeradminpanel.R
import com.example.nafis.nf2024.organizeradminpanel.databinding.SubjectItemBinding

class SubjectViewHolder(
    var binding:SubjectItemBinding, var context: Context
    , var callback: SubjectCallback
):RecyclerView.ViewHolder(binding.root) {

    fun bind(item:Subject){
        binding.apply {
            CourseSubName.text=item.subjectName
            when(item.subjectName){
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
            callback.onSubjectClick(item)
        }
        binding.moreOptionSubject.setOnClickListener {
            popMenuOpen(item,it)
        }
    }

    private fun popMenuOpen(item: Subject, view: View) {
        val popupMenu=PopupMenu(context,view)
        popupMenu.inflate(R.menu.courseitem_more_option)



        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.pop_delete->{
                    callback.subjectDelete(item.subjectId)
                    true
                }
                R.id.pop_update->{
                    callback.subjectUpdate(item)
                    true
                }
                else ->false
            }
        }
        popupMenu.show()
    }
}
