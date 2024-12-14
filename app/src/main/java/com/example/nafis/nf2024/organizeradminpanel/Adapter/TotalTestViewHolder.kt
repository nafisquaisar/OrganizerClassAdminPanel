package com.example.nafis.nf.organizetestcenter.Adapter

import android.content.Context
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.nafis.nf2024.organizeradminpanel.Model.TestObject
import com.example.nafis.nf2024.organizeradminpanel.R
import com.example.nafis.nf2024.organizeradminpanel.databinding.TestItemBinding
import com.example.nafisquaisarcoachingcenter.DIffUtilCallBack.TotalTestItemCallback


class TotalTestViewHolder(val binding: TestItemBinding, private val callback: TotalTestItemCallback,
                          val context: Context
  ): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: TestObject){
              binding.apply {
                      itemTotalMark.text= "${data.totalMark} Marks | "
                      itemTotalTime.text="${data.totalTime} Minutes"
                      itemNoOfQues.text= "${data.noOfQues} Questions |"
                      itemTestTitle.text=data.title
                      itemView.setOnClickListener {
                          callback.onTotalTestClick(data,position)
                      }
                    itemClickMore.setOnClickListener {
                        popDialog(data)
                    }

              }
        }

    private fun popDialog(data: TestObject) {
        val dialog = PopupMenu(context, binding.itemTestTitle)
        dialog.inflate(R.menu.courseitem_more_option)

        // Show the PopupMenu
        dialog.show()

        dialog.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.pop_update -> {
                    callback.updateTotalTestClick(data)
                    true
                }
                R.id.pop_delete -> {
                    // Handle the "markDone" functionality here
                    callback.deleteTotalTestClick(data.id)
                    true
                }
                else -> false
            }
        }
    }

}
