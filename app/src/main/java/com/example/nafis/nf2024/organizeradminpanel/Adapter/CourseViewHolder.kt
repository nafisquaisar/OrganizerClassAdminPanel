package com.example.nafis.nf2024.organizeradminpanel.Adapter

import android.content.Context
import android.graphics.Paint
import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack.CouseCallback
import com.example.nafis.nf2024.organizeradminpanel.Model.CourseModel
import com.example.nafis.nf2024.organizeradminpanel.R
import com.example.nafis.nf2024.organizeradminpanel.databinding.CourseItemBinding

class CourseViewHolder(var binding:CourseItemBinding,var context: Context
   , var callback: CouseCallback
):RecyclerView.ViewHolder(binding.root) {

    fun bind(item:CourseModel){
        binding.apply {
            courseTitle.text=item.courseName
            coursePrice.text="₹ ${item.courseAmount}"
            courseOfferPrice.text="₹ ${item.offerAmount}"
            coursePrice.paintFlags = coursePrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
             courseTitle.isSelected=true
            Glide.with(context)
                .load(item.courseImgUrl)
                .placeholder(R.drawable.biharboardbanner)
                .into(courseImg)
        }

        itemView.setOnClickListener {
            callback.onCourseClick(item)
        }

        binding.more.setOnClickListener {
            openPopMenu(item,it)
        }
    }

    private fun openPopMenu(item: CourseModel, view: View) {
        val popUpMenu = PopupMenu(context, view)
        popUpMenu.inflate(R.menu.course_more_option)

        // Dynamically set the text for the 'action_disable' menu item
        val disableMenuItem = popUpMenu.menu.findItem(R.id.action_disable)
        disableMenuItem.title = if (item.isCourseDisable) "Enable" else "Disable"

        popUpMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_update -> {
                    callback.onCourseUpdate(item)
                    true
                }
                R.id.action_delete -> {
                    item.courseId.let { it1 -> deleteCourse(it1) }
                    true
                }
                R.id.action_disable -> {
                    callback.onCourseDisable(item.courseId)
                    true
                }
                else -> false
            }
        }
        popUpMenu.show()
    }

    private fun deleteCourse(it1: String) {
          callback.onCourseDelete(it1)
    }
}
