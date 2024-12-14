package com.example.nafis.nf2024.organizeradminpanel.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.nafis.nf2024.organizeradminpanel.MainActivity
import com.example.nafis.nf2024.organizeradminpanel.Obj
import com.example.nafis.nf2024.organizeradminpanel.R
import com.example.nafis.nf2024.organizeradminpanel.databinding.FragmentTestPanelBinding


class TestPanelFragment : Fragment() {
  private lateinit var binding: FragmentTestPanelBinding
    private var selectedClass=""
    private var  selectedSubject=""
    private  var selectedChap=""
    private   var selectedTime=""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentTestPanelBinding.inflate(inflater,container,false)


        MainActivity.binding.backarrow.visibility = View.VISIBLE
        MainActivity.binding.titleName.text="Test Upload Pannel"
        MainActivity.binding.backarrow.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val classes = Obj.classes()

        // Set up the class spinner
        setupTimeSpinner()
        setupClassSpinner(classes)

        binding.nextBtn.setOnClickListener {
            val title=binding.testTitle.text.toString()
            val mark=binding.totalmark.text.toString()
            val noOfques=binding.noOfQuestion.text.toString()
            if (title.isNotEmpty() &&
                mark.isNotEmpty() &&
                noOfques.isNotEmpty() &&
                selectedClass.isNotEmpty() && selectedClass != "Select Class" &&
                selectedSubject.isNotEmpty() && selectedSubject != "Select Subject" &&
                selectedChap.isNotEmpty() && selectedChap != "Select Chapter" && selectedChap != "No Chapter Available" &&
                selectedTime.isNotEmpty() && selectedTime != "No time Available" && selectedTime != "Select Time") {

                val bundle = Bundle().apply {
                     putString("argTitle", title)
                     putString("argTotalMark", mark)
                     putString("argQuestionCount", noOfques)
                     putString("argClassName", selectedClass)
                     putString("argSubjectName", selectedSubject)
                     putString("argChapterName", selectedChap)
                     putString("argTimeDuration", selectedTime)
                 }
                 findNavController().navigate(R.id.action_testPanelFragment_to_testQuestionFragment, bundle)
             }else{
                 Toast.makeText(requireContext(),"Fill all Detail" ,Toast.LENGTH_SHORT).show()
             }
        }

        return binding.root
    }

    private fun setupClassSpinner(classes: Array<String>) {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            classes
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.classspinner.adapter = adapter

        // Handle class selection
        binding.classspinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedClass = parent.getItemAtPosition(position).toString()

                if (position != 0) { // Ignore the default "Select Class" option
                    setupSubjectSpinner(selectedClass) // Dynamically update the subject spinner
                } else {
                    // Reset subject spinner for "Select Class"
                    setupSubjectSpinner("") // No subjects for default option
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle no selection
            }
        }
    }

    private fun setupSubjectSpinner(selectedClass: String) {
        val subjects = if (selectedClass.isNotEmpty()) {
            Obj.Subject(selectedClass) // Get subjects based on selected class
        } else {
            arrayOf("Select Subject") // Default option if no class is selected
        }

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            subjects
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.subjectspinner.adapter = adapter

        // Handle subject selection
        binding.subjectspinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedSubject = parent.getItemAtPosition(position).toString()
                if (position != 0) { // Ignore the default "Select Subject" option
                    setupChapterSpinner(selectedClass,selectedSubject)
                }else{
                    setupChapterSpinner("","")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle no selection
            }
        }
    }

    private fun setupTimeSpinner() {
        val times = Obj.Time() // Always fetch available times from Obj.Time()

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            if (times.isNotEmpty()) times else arrayOf("No time Available")
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.totalTimeSpinner.adapter = adapter

        // Restore previously selected time if available
        val previousSelectionIndex = times.indexOf(selectedTime)
        if (previousSelectionIndex != -1) {
            binding.totalTimeSpinner.setSelection(previousSelectionIndex)
        }

        binding.totalTimeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedTime = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle no selection
            }
        }
    }


    fun setupChapterSpinner(clas:String,sub:String){

        val chapters=if(clas.isNotEmpty() && sub.isNotEmpty()){
            Obj.Chapter(clas,sub)
        }else{
            arrayOf("Select Chapter")
        }

        var adapter= ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            chapters
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.chapterspinner.adapter=adapter
        binding.chapterspinner.onItemSelectedListener=object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedChap=p0?.getItemAtPosition(p2).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        MainActivity.binding.backarrow.visibility = View.GONE
        MainActivity.binding.titleName.setText(R.string.organizer_admin_panel)
    }
}