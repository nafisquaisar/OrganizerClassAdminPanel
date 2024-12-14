package com.example.nafis.nf2024.organizeradminpanel.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.nafis.nf2024.organizeradminpanel.CourseTestFragment
import com.example.nafis.nf2024.organizeradminpanel.Fragment.SubjectsFragment

class CourseClassTestPagerAdapter(
    fragmentmanager: FragmentManager, lifecycle: Lifecycle, var courseId: String?,var courseName: String?
):FragmentStateAdapter(fragmentmanager,lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
          return  if(position==0){
                    SubjectsFragment(courseId,courseName)
           }else{
                  CourseTestFragment(courseId,courseName)
           }
    }
}