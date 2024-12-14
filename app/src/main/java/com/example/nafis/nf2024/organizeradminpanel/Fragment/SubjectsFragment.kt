package com.example.nafis.nf2024.organizeradminpanel.Fragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nafis.nf2024.organizeradminpanel.Adapter.SubjectAdapter
import com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack.SubjectCallback
import com.example.nafis.nf2024.organizeradminpanel.MainActivity
import com.example.nafis.nf2024.organizeradminpanel.Model.Subject
import com.example.nafis.nf2024.organizeradminpanel.R
import com.example.nafis.nf2024.organizeradminpanel.databinding.FragmentSubjectsBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class SubjectsFragment(var CourseId: String?=null, var CourseName: String?=null) : Fragment() {
    private lateinit var binding: FragmentSubjectsBinding
    private lateinit var subjectAdapter: SubjectAdapter
    private lateinit var subList: ArrayList<Subject>
    private var db = FirebaseFirestore.getInstance()
    private val callback by lazy {
        object : SubjectCallback {
            override fun onSubjectClick(item: Subject) {
                // Handle subject click here
                val parent = parentFragment as? CourseClassAndTestFragment
                val bundle=Bundle().apply {
                    putString("CourseId",CourseId)
                    putString("CourseName",CourseName)
                    putString("Subject",item.subjectId)
                }
                parent?.findNavController()?.navigate(R.id.action_courseClassAndTestFragment_to_chapterFragment,bundle)
            }

            override fun subjectDelete(itemId: String) {
                deleteSubject(itemId)
            }

            override fun subjectUpdate(item: Subject) {
                  addSubject(item,"Update")
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSubjectsBinding.inflate(inflater, container, false)

        MainActivity.binding.backarrow.visibility = View.VISIBLE
        MainActivity.binding.titleName.text = CourseName
        MainActivity.binding.backarrow.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        subList = ArrayList()
        subjectAdapter = SubjectAdapter(requireContext(), callback, subList)
            binding.SubjectRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.SubjectRecyclerView.adapter = subjectAdapter


        Toast.makeText(requireContext(),CourseId,Toast.LENGTH_SHORT).show()
        CourseId?.let { Log.d("CourseId", it) }
        fetchSubjects(CourseId!!)

        binding.addSubject.setOnClickListener {
            addSubject()
        }

        return binding.root
    }



    //=================Delete Subject================
    private fun deleteSubject(itemId: String) {
        val subRef = db.collection("courses").document(CourseId!!).collection("subjects").document(itemId)

        // Show a confirmation dialog
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Subject")
            .setMessage("Are you sure you want to delete this subject?")
            .setPositiveButton("Yes") { _, _ ->
                // Delete the subject document
                subRef.delete()
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Subject deleted successfully", Toast.LENGTH_SHORT).show()
                        // Refresh the list and notify the adapter
                        fetchSubjects(CourseId!!)
                        subjectAdapter.notifyDataSetChanged()
                    }
                    .addOnFailureListener { error ->
                        Toast.makeText(requireContext(), "Failed to delete: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("No", null)
            .show()
    }





    //=================Add Subject================
    private fun addSubject(item: Subject? =null, isUpdate: String="") {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.add_subject_dailog)
        dialog.show()

        val subName = dialog.findViewById<EditText>(R.id.DialogSubTitle)
        val addBtn = dialog.findViewById<Button>(R.id.addSubBtn)
        val cancelBtn = dialog.findViewById<Button>(R.id.cancelSubBtn)

        if(item!=null && isUpdate=="Update" ){
            addBtn.setText(isUpdate)
            subName.setText(item.subjectName)

            addBtn.setOnClickListener {
                val updateSubjectName = subName.text.toString().trim()
                if (updateSubjectName.isEmpty()) {
                    Toast.makeText(requireContext(), "Please enter a subject name", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val subjectRef = db.collection("courses")
                    .document(CourseId!!)
                    .collection("subjects")
                    .document(item.subjectId)
                val subject = hashMapOf(
                    "subjectId" to item.subjectId,
                    "subjectName" to updateSubjectName
                )
                subjectRef.update(subject as Map<String,Any>)
                    .addOnSuccessListener {
                        fetchSubjects(CourseId!!)
                        dialog.dismiss()
                        Toast.makeText(requireContext(), "Subject updated successfully!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Failed to update subject: ${e.message}", Toast.LENGTH_SHORT).show()
                    }

            }

        }else{
            addBtn.setOnClickListener {
                val subjectName = subName.text.toString().trim()
                if (subjectName.isEmpty()) {
                    Toast.makeText(requireContext(), "Please enter a subject name", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val subId=getSubId(subjectName)
                // Set the subjectName as the document ID
                val subjectRef = db.collection("courses")
                    .document(CourseId!!)
                    .collection("subjects")
                    .document(subId)

                val subject = hashMapOf(
                    "subjectId" to subId,
                    "subjectName" to subjectName
                )

                subjectRef.set(subject)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Subject added successfully!", Toast.LENGTH_SHORT).show()
                        fetchSubjects(CourseId!!) // Refresh the list
                        dialog.dismiss()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Failed to add subject: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }






        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }
    }

    //=================Fetch Subject================
    private fun fetchSubjects(courseId: String) {
        binding.progressbar.visibility = View.VISIBLE
        subList.clear()
        db.collection("courses")
            .document(courseId)
            .collection("subjects")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val subject = document.toObject(Subject::class.java)
                    subList.add(subject)
                }
                if (subList.isEmpty()) {
                    binding.progressbar.visibility = View.GONE
                    binding.helping.visibility=View.VISIBLE
                } else {
                    subjectAdapter.submitList(subList)
                    subjectAdapter.notifyDataSetChanged()
                    binding.progressbar.visibility = View.GONE
                    binding.helping.visibility=View.GONE
                }

            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to fetch subjects: ${e.message}", Toast.LENGTH_SHORT).show()
                binding.progressbar.visibility = View.GONE
                binding.helping.visibility=View.VISIBLE
            }
    }

    private fun getSubId(subjectName:String):String{
         val sanitizedSubjectName=subjectName.replace(Regex("[^a-zA-Z0-9]"), "")
        return "$sanitizedSubjectName-${UUID.randomUUID()}"
    }
}
