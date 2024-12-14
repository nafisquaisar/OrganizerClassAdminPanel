package com.example.nafis.nf2024.organizeradminpanel.Fragment

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nafis.nf2024.organizeradminpanel.Adapter.ChapterAdapter
import com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack.ChapterCallback
import com.example.nafis.nf2024.organizeradminpanel.MainActivity
import com.example.nafis.nf2024.organizeradminpanel.Model.Chapter

import com.example.nafis.nf2024.organizeradminpanel.R
import com.example.nafis.nf2024.organizeradminpanel.databinding.FragmentChapterBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class ChapterFragment : Fragment() {
  private lateinit var binding: FragmentChapterBinding
 private var courseId:String=""
 private var courseName:String=""
 private var subjectId:String=""
    private var db = FirebaseFirestore.getInstance()
    private lateinit var chapterList:ArrayList<Chapter>
    private lateinit var chapAdapter:ChapterAdapter

    private val callback by lazy {
        object:ChapterCallback{
            override fun onChapterClick(item: Chapter) {
              val bundle =Bundle().apply {
                  putString("courseId",courseId)
                  putString("courseName",courseName)
                  putString("subjectId",subjectId)
                  putString("CourseChapter",item.chapId)
              }
                findNavController().navigate(R.id.action_chapterFragment_to_courseContentFragment,bundle)
            }

            override fun onUpdateDoubt(item: Chapter) {
                  addChapterDialog(item,true)
            }

            override fun deleteCourseChapter(chapterId: String) {
                deleteChapter(chapterId)
            }

        }
    }

    private fun deleteChapter(chapterId: String) {
        val chapterRef = db.collection("courses")
            .document(courseId)
            .collection("subjects")
            .document(subjectId)
            .collection("Chapters")
            .document(chapterId)

        AlertDialog.Builder(requireContext())
            .setTitle("Delete Chapter")
            .setMessage("Are you sure you want to delete this chapter?")
            .setPositiveButton("Yes") { _, _ ->
                chapterRef.delete()
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Chapter deleted successfully", Toast.LENGTH_SHORT).show()
                        fetchChapters() // Refresh the list
                        chapAdapter.notifyDataSetChanged()
                    }
                    .addOnFailureListener { error ->
                        Toast.makeText(requireContext(), "Failed to delete: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("No", null)
            .show()
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentChapterBinding.inflate(inflater,container,false)
        courseId= arguments?.getString("CourseId").toString()
        courseName= arguments?.getString("CourseName").toString()
        subjectId= arguments?.getString("Subject").toString()

        Log.d("CourseId",courseId)

        MainActivity.binding.backarrow.visibility = View.VISIBLE
        MainActivity.binding.titleName.text = courseName
        MainActivity.binding.backarrow.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        chapterList= ArrayList()
        chapAdapter= ChapterAdapter(requireContext(),callback,chapterList)
        binding.chapterRecyclerView.layoutManager=LinearLayoutManager(requireContext())
        binding.chapterRecyclerView.adapter=chapAdapter


        binding.addchapter.setOnClickListener {
            addChapterDialog(isUpdate = false)
        }
        fetchChapters()

        return binding.root
    }

    private fun getChapId(chapName:String): String {
        val sanatizedChapName=chapName.replace(Regex("[^a-zA-Z0-9]"),"")
        return "${sanatizedChapName}-${UUID.randomUUID()}"
    }


    private fun addChapterDialog(item: Chapter? = null, isUpdate: Boolean) {
        val dialog=Dialog(requireContext())
        dialog.setContentView(R.layout.add_subject_dailog)

        dialog.show()

        val chapNameEdit=dialog.findViewById<EditText>(R.id.DialogSubTitle)
        val saveChapBtn=dialog.findViewById<Button>(R.id.addSubBtn)
        val cancelBtn=dialog.findViewById<Button>(R.id.cancelSubBtn)

        if(isUpdate && item!=null){
            saveChapBtn.setText("Update")
            chapNameEdit.setText(item.chapName)

             saveChapBtn.setOnClickListener {
                 val updateChapName=chapNameEdit.text.toString().trim()
                 if (updateChapName.isEmpty()) {
                     Toast.makeText(requireContext(), "Please enter a chapter name", Toast.LENGTH_SHORT).show()
                     return@setOnClickListener
                 }
                 val chapterRef = db.collection("courses")
                     .document(courseId)
                     .collection("subjects")
                     .document(subjectId)
                     .collection("Chapters")
                     .document(item.chapId)
                 val chapter= hashMapOf(
                     "chapId" to item.chapId,
                     "chapName" to updateChapName
                 )

                 chapterRef.update(chapter as Map<String,Any>)
                     .addOnSuccessListener {
                         Toast.makeText(requireContext(), "Chapter added successfully!", Toast.LENGTH_SHORT).show()
                         fetchChapters() // Refresh the list
                         dialog.dismiss()
                     }
                     .addOnFailureListener{
                         Toast.makeText(requireContext(), "Failed to add chapter: ${it.message}", Toast.LENGTH_SHORT).show()
                     }
             }
        }else{
            saveChapBtn.setOnClickListener {
                val chapName=chapNameEdit.text.toString().trim()

                if (chapName.isEmpty()) {
                    Toast.makeText(requireContext(), "Please enter a chapter name", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val chapId=getChapId(chapName)
                Log.d("courseId",courseId)
                val chapterRef = db.collection("courses")
                    .document(courseId)
                    .collection("subjects")
                    .document(subjectId)
                    .collection("Chapters")
                    .document(chapId)
                val chapter= hashMapOf(
                    "chapId" to chapId,
                    "chapName" to chapName
                )

                chapterRef.set(chapter)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Chapter added successfully!", Toast.LENGTH_SHORT).show()
                        fetchChapters() // Refresh the list
                        dialog.dismiss()
                    }
                    .addOnFailureListener{
                        Toast.makeText(requireContext(), "Failed to add chapter: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }




        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

    }

    private fun fetchChapters() {
        binding.progressbar.visibility = View.VISIBLE
        chapterList.clear()
        db.collection("courses")
            .document(courseId)
            .collection("subjects")
            .document(subjectId)
            .collection("Chapters")
            .get()
            .addOnSuccessListener {Chapters->
                for(chap in Chapters){
                    val chapter=chap.toObject(Chapter::class.java)
                    chapterList.add(chapter)
                }
                if (chapterList.isEmpty()) {
                   binding.helping.visibility=View.VISIBLE
                } else {
                    chapAdapter.submitList(chapterList)
                    chapAdapter.notifyDataSetChanged()
                    binding.helping.visibility=View.GONE
                }
                binding.progressbar.visibility = View.GONE
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to fetch Subject: ${it.message}", Toast.LENGTH_SHORT).show()
                binding.progressbar.visibility = View.GONE
                binding.helping.visibility=View.VISIBLE
            }

    }




}