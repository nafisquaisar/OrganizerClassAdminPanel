package com.example.nafis.nf2024.organizeradminpanel.Adapter

import androidx.recyclerview.widget.RecyclerView
import com.example.nafis.nf2024.organizeradminpanel.Model.QuestionList
import com.example.nafis.nf2024.organizeradminpanel.Model.QuizModel
import com.example.nafis.nf2024.organizeradminpanel.databinding.QuestionItemViewBinding

class QuestionListView(var binding: QuestionItemViewBinding):RecyclerView.ViewHolder(binding.root) {
       fun bind(item:QuizModel){
             binding.Question.text=item.quizQues
             binding.questionNo.text="Q ${item.quizId})"
       }
}
