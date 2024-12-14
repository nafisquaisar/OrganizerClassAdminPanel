package com.example.nafis.nf2024.organizeradminpanel.Fragment

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.nafis.nf2024.organizeradminpanel.Obj
import com.example.nafis.nf2024.organizeradminpanel.MainActivity
import com.example.nafis.nf2024.organizeradminpanel.Model.VideoModel
import com.example.nafis.nf2024.organizeradminpanel.ProgressDialogUtil
import com.example.nafis.nf2024.organizeradminpanel.R
import com.example.nafis.nf2024.organizeradminpanel.databinding.FragmentVideoBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Objects
import java.util.UUID


class VideoFragment : Fragment() {
    private lateinit var binding: FragmentVideoBinding
    private var selectedClass=""
    private var  selectedSubject=""
    private  var selectedChap=""
    private   var selectedType=""
    private  var title=""
    private  var url=""
    private lateinit var list: ArrayList<VideoModel>
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVideoBinding.inflate(inflater, container, false)

        MainActivity.binding.backarrow.visibility = View.VISIBLE
        MainActivity.binding.titleName.text="Video Upload Pannel"
        MainActivity.binding.backarrow.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val classes = Obj.classes()

        // Set up the class spinner
        setupClassSpinner(classes)
//        setUpType()
        binding.uplaodVideoBtn.setOnClickListener {
            uploadData()
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


    fun setupChapterSpinner(clas:String,sub:String){

        val chapters=if(clas.isNotEmpty() && sub.isNotEmpty()){
            Obj.Chapter(clas,sub)
        }else{
            arrayOf("Select Chapter")
        }

        var adapter=ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            chapters
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.chapterspinner.adapter=adapter
        binding.chapterspinner.onItemSelectedListener=object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedChap=p0?.getItemAtPosition(p2).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun uploadData() {
        // Get data from input fields
        val title = binding.videoTitle.text.toString().trim()
        val url = binding.VideoUrl.text.toString().trim()
        val des=binding.videoDes.text.toString()
        if (title.isEmpty() || url.isEmpty() || des.isEmpty() || selectedClass.isEmpty()&& selectedClass=="Select Class" ||
            selectedSubject.isEmpty() || selectedSubject=="Select Subject"|| selectedChap.isEmpty() || selectedChap=="Select Chapter" || selectedChap=="No Chapter Available") {
            Toast.makeText(requireContext(), "Feild cannot be empty", Toast.LENGTH_SHORT).show()
        }else{
            // Generate unique ID and get the current date
            val id = generateLongFromUUID()
            val date = getCurrentDate()
            // Create a single VideoModel object
            val videoModel = VideoModel(id, title,des, date, "", selectedChap, selectedClass, selectedSubject, url)
            ProgressDialogUtil.showProgress(requireContext(),0)

            // Initialize Firestore
            val firestore = Firebase.firestore

            // Upload the videoModel to Firestore
            firestore.collection("Class")
                .document(selectedClass)
                .collection("Subjects")
                .document(selectedSubject)
                .collection("Chapters")
                .document(selectedChap)
                .collection("Video")
                .document(id.toString()) // Use generated ID as the document ID
                .set(videoModel)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Video uploaded successfully!", Toast.LENGTH_SHORT).show()
                    resetFeild()
                    ProgressDialogUtil.dismissProgressDialog()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), "Failed to upload video: ${exception.message}", Toast.LENGTH_SHORT).show()
                    ProgressDialogUtil.dismissProgressDialog()
                }
        }


    }

    private fun resetFeild() {
        binding.videoTitle.text.clear()
        binding.videoDes.text.clear()
        binding.VideoUrl.text.clear()
        binding.classspinner.setSelection(0)
        binding.subjectspinner.setSelection(0)
        binding.chapterspinner.setSelection(0)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        MainActivity.binding.backarrow.visibility = View.GONE
        MainActivity.binding.titleName.setText(R.string.organizer_admin_panel)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDate(): String {
        val currentDate = LocalDate.now()
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return currentDate.format(dateFormatter)
    }


    fun generateLongFromUUID(): Long {
        val uuid = UUID.randomUUID()
        return uuid.leastSignificantBits // Extract least significant bits
    }



//    fun setUpType(){
//
//        val types=Obj.Type()
//        var adapter=ArrayAdapter(
//            requireContext(),
//            android.R.layout.simple_spinner_item,
//            types
//        )
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        binding.typespinner.adapter=adapter
//        binding.typespinner.onItemSelectedListener=object :AdapterView.OnItemSelectedListener{
//            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                selectedType=p0?.getItemAtPosition(p2).toString()
//                if(p2!=0){
//                    Toast.makeText(requireContext(), "Subject Selected: $selectedType", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onNothingSelected(p0: AdapterView<*>?) {
//                TODO("Not yet implemented")
//            }
//
//        }
//    }

}
