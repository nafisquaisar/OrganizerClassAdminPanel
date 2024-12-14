package com.example.nafis.nf2024.organizeradminpanel.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.nafis.nf2024.organizeradminpanel.DppFragmentView
import com.example.nafis.nf2024.organizeradminpanel.NoteFragmentView
import com.example.nafis.nf2024.organizeradminpanel.VideoFragmentView


class ViewPagerAdapter(fragmentmanager:FragmentManager,lifecycle:Lifecycle,
                       var courseId:String?,
                       var courseName:String?,
                       var subjectId:String?,
                       var chapterId:String?

):FragmentStateAdapter(fragmentmanager,lifecycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return if(position==0){
            VideoFragmentView(courseId,courseName,subjectId,chapterId)
        }else if(position==1){
            NoteFragmentView(courseId,courseName,subjectId,chapterId)
        }else{
            DppFragmentView(courseId,courseName,subjectId,chapterId)
        }
    }
}