package com.example.nafis.nf2024.organizeradminpanel.Adapter

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import com.example.nafis.nf2024.organizeradminpanel.Fragment.TestQuestionFragment
import com.example.nafis.nf2024.organizeradminpanel.Model.QuestionList
import com.example.nafis.nf2024.organizeradminpanel.Model.QuizModel
import com.example.nafis.nf2024.organizeradminpanel.Model.QuizOption
import com.example.nafis.nf2024.organizeradminpanel.R
import com.example.nafis.nf2024.organizeradminpanel.databinding.QuestionItemViewBinding

class QuestionListAdapter(var context: Context,var list:ArrayList<QuizModel>,
                          private val fragment: TestQuestionFragment
                          ,private val onQuestionUpdated: (QuizModel) -> Unit):Adapter<QuestionListView> (){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionListView {
          val view=QuestionItemViewBinding.inflate(LayoutInflater.from(context),parent,false)
          return QuestionListView(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: QuestionListView, position: Int) {
          val model=list.get(position)
         holder.bind(model)

        holder.itemView.setOnClickListener {
            fragment.UpdateDilogshowDialog(model,position,model.imgUrl)
        }
    }

    // Function to remove an item from the list
    fun removeItem(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }



}