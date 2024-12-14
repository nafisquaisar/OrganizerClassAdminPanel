package com.example.nafis.nf2024.organizeradminpanel.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.nafis.nf2024.organizeradminpanel.Adapter.CourseClassTestPagerAdapter
import com.example.nafis.nf2024.organizeradminpanel.MainActivity
import com.example.nafis.nf2024.organizeradminpanel.databinding.FragmentCourseClassAndTestBinding
import com.google.android.material.tabs.TabLayoutMediator

class CourseClassAndTestFragment : Fragment() {
   private lateinit var binding:FragmentCourseClassAndTestBinding
    private lateinit var adapter: CourseClassTestPagerAdapter
    private var courseId:String?=null
    private var courseName:String?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentCourseClassAndTestBinding.inflate(inflater,container,false)

        courseId= arguments?.getString("courseId")
        courseName=arguments?.getString("courseName")

        adapter = CourseClassTestPagerAdapter(childFragmentManager, lifecycle,courseId=courseId,courseName=courseName)
        binding.lectureContainer.adapter = adapter

        MainActivity.binding.backarrow.visibility = View.VISIBLE
        MainActivity.binding.titleName.text = courseName
        MainActivity.binding.backarrow.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }


        // Link TabLayout and ViewPager2 using TabLayoutMediator
        TabLayoutMediator(binding.tablayout, binding.lectureContainer) { tab, position ->
            tab.text = if (position == 0) "All Class" else "Test"
        }.attach()

        return binding.root
    }

}