package com.example.nafis.nf2024.organizeradminpanel.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.nafis.nf2024.organizeradminpanel.Adapter.ViewPagerAdapter
import com.example.nafis.nf2024.organizeradminpanel.MainActivity

import com.example.nafis.nf2024.organizeradminpanel.databinding.FragmentCourseContentBinding
import com.google.android.material.tabs.TabLayout


class CourseContentFragment : Fragment() {
   private lateinit var binding:FragmentCourseContentBinding
    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var adapter: ViewPagerAdapter
    private var CourseName:String=""
    private var CourseId:String=""
    private var CourseSubject:String=""
    private var CourseChapter:String=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentCourseContentBinding.inflate(inflater,container,false)
        CourseName= arguments?.getString("courseName").toString()
        CourseId= arguments?.getString("courseId").toString()
        CourseSubject= arguments?.getString("subjectId").toString()
        CourseChapter= arguments?.getString("CourseChapter").toString()
        MainActivity.binding.backarrow.visibility = View.VISIBLE
        MainActivity.binding.titleName.text = CourseName
        MainActivity.binding.backarrow.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // =========Initialize ViewPager2 and TabLayout=========
        viewPager2 = binding.lectureContainer
        tabLayout = binding.tablayout



        // ============set adapter for viewpager2============
        val fragmentManager = childFragmentManager
        adapter = ViewPagerAdapter(fragmentManager, lifecycle,CourseId,CourseName,CourseSubject,CourseChapter)
        viewPager2.adapter = adapter



        //=========set tab=====================
        tabLayout.addTab(tabLayout.newTab().setText("Video"))
        tabLayout.addTab(tabLayout.newTab().setText("Note"))
        tabLayout.addTab(tabLayout.newTab().setText("DPP"))
        viewPager2.adapter=adapter

        //============== set view pager with tab============
        tabLayout.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if(tab!=null){
                    viewPager2.currentItem=tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

        viewPager2.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })

        return binding.root
    }


}