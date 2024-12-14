package com.example.nafis.nf2024.organizeradminpanel

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import com.example.nafis.nf2024.organizeradminpanel.Model.TestObject
import com.example.nafis.nf2024.organizeradminpanel.databinding.FragmentCourseTestBinding
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import java.util.UUID


class CourseTestFragment(var courseId: String?, var courseName: String?) : Fragment() {
   private lateinit var binding:FragmentCourseTestBinding
    private   var selectedTime=""
    private lateinit var  dialog:Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentCourseTestBinding.inflate(inflater,container,false)

        binding.courseTestAdd.setOnClickListener {
            addTestDialog()
        }

        fetchTest()
        return binding.root
    }

    private fun fetchTest() {

    }

    private fun addTestDialog() {
        dialog= Dialog(requireContext())
        dialog.setContentView(R.layout.add_course_test_dialog)
        dialog.show()
        val testNameEdit=dialog.findViewById<EditText>(R.id.dialogTestTitle)
        val testTotalMarkEdit=dialog.findViewById<EditText>(R.id.dialogTotalmark)
        val testNoOfQuesEdit=dialog.findViewById<EditText>(R.id.dialogNoOfQuestion)
        setupTimeSpinner()
        val saveChapBtn=dialog.findViewById<Button>(R.id.dialogUplaodTestBtn)
        val cancelBtn=dialog.findViewById<Button>(R.id.dialogCancelTestBtn)

        saveChapBtn.setOnClickListener {
            val testTitle=testNameEdit.text.toString().trim()
            val totalMark=testTotalMarkEdit.text.toString().trim()
            val noOfQues=testNoOfQuesEdit.text.toString().trim()
            val id=getTestId(testTitle)
            val db = FirebaseDatabase.getInstance().getReference("CourseTest").child(courseId!!).child(id)

            val test = TestObject(
                id = id,
                title = testTitle,
                noOfQues = noOfQues,
                totalMark = totalMark,
                classname = courseName!!,
                subname = "",
                chapname = ""
            )

            db.setValue(test).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Firebase", "Test object created successfully!")

                } else {
                    Log.e("Firebase", "Error creating test object: ${task.exception?.message}")
                }
            }
            dialog.dismiss()

        }

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

    }

    private fun getTestId(testName:String): String {
        val sanatizedTestName=testName.replace(Regex("[^a-zA-Z0-9]"),"")
        return "${sanatizedTestName}-${UUID.randomUUID()}"
    }


    private fun setupTimeSpinner() {
        val times = Obj.Time() // Always fetch available times from Obj.Time()

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            if (times.isNotEmpty()) times else arrayOf("No time Available")
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val totalTimeSpinner=dialog.findViewById<Spinner>(R.id.dialogTotalTimeSpinner)
        totalTimeSpinner.adapter = adapter

        // Restore previously selected time if available
        val previousSelectionIndex = times.indexOf(selectedTime)
        if (previousSelectionIndex != -1) {
            totalTimeSpinner.setSelection(previousSelectionIndex)
        }

        totalTimeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

}