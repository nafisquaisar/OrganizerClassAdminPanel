package com.example.nafis.nf2024.organizeradminpanel.Fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.nafis.nf2024.organizeradminpanel.MainActivity
import com.example.nafis.nf2024.organizeradminpanel.Model.NoteModel
import com.example.nafis.nf2024.organizeradminpanel.Obj
import com.example.nafis.nf2024.organizeradminpanel.ProgressDialogUtil
import com.example.nafis.nf2024.organizeradminpanel.R
import com.example.nafis.nf2024.organizeradminpanel.databinding.FragmentNoteBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID


class NoteFragment : Fragment() {
 private  lateinit var binding: FragmentNoteBinding
    private var selectedClass=""
    private var  selectedSubject=""
    private  var selectedChap=""
    private  var title=""
    private var selectedPdfUri: Uri? = null
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentNoteBinding.inflate(inflater,container,false)

        MainActivity.binding.backarrow.visibility = View.VISIBLE
        MainActivity.binding.titleName.text="Note Upload Pannel"
        MainActivity.binding.backarrow.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val classes = Obj.classes()

        // Set up the class spinner
        setupClassSpinner(classes)

        binding.uplaodNoteBtn.setOnClickListener {
            uploadData()
        }

        binding.animationViewNote.setOnClickListener {
            openFilePicker()
        }
        return binding.root
    }

    //==============CALSS SPINNER=====================
    private fun setupClassSpinner(classes: Array<String>) {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            classes
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.classspinnerNote.adapter = adapter

        // Handle class selection
        binding.classspinnerNote.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
    //==============CALSS SPINNER=====================

    //==============SUBJECT SPINNER=====================
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
        binding.subjectspinnerNote.adapter = adapter

        // Handle subject selection
        binding.subjectspinnerNote.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
    //==============SUBJECT SPINNER=====================


    //==============CHAPTER SPINNER=====================
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
        binding.chapterspinnerNote.adapter=adapter
        binding.chapterspinnerNote.onItemSelectedListener=object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedChap=p0?.getItemAtPosition(p2).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }
    //==============CHAPTER SPINNER=====================



    //==============UPLOAD DATA TO FIREBASE=====================

    @RequiresApi(Build.VERSION_CODES.O)
    private fun uploadData() {
        title = binding.NoteTitle.text.toString()

        if (title.isEmpty() ||  selectedClass.isEmpty()&& selectedClass=="Select Class" ||
            selectedSubject.isEmpty() || selectedSubject=="Select Subject"|| selectedChap.isEmpty() ||
            selectedChap=="Select Chapter" || selectedChap=="No Chapter Available"){
            Toast.makeText(requireContext(), "Feild cannot be empty", Toast.LENGTH_SHORT).show()
             return
        }

        // Validate file selection
        if (selectedPdfUri == null) {
            Toast.makeText(requireContext(), "Pick the Note", Toast.LENGTH_SHORT).show()
            return
        }

        val databaseStorage = FirebaseStorage.getInstance()
        val reference = databaseStorage.reference.child("Note/$selectedClass/$selectedSubject/$title")

        val uploadTask = selectedPdfUri?.let { reference.putFile(it) }

        // Show the progress dialog before the upload begins
        if (uploadTask != null) {
            ProgressDialogUtil.showProgressDialog(requireContext(), uploadTask)
        }

        // Monitor upload progress
        uploadTask?.addOnProgressListener { taskSnapshot ->
            // Track progress (no need to call ProgressDialogUtil.showProgressDialog here)
            val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
            ProgressDialogUtil.updateProgress(progress)
        }

        // Handle successful upload
        uploadTask?.addOnSuccessListener {
            reference.downloadUrl.addOnSuccessListener { downloadUri ->
                uploadUrl(downloadUri)
            }
        }

        // Handle upload failure
        uploadTask?.addOnFailureListener { exception ->
            Log.e("Firebase", "Upload failed: ${exception.message}")
            Toast.makeText(requireContext(), "Upload failed: ${exception.message}", Toast.LENGTH_SHORT).show()

            // Dismiss the progress dialog in case of failure
            activity?.runOnUiThread {
                ProgressDialogUtil.dismissProgressDialog()
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun uploadUrl(downloadUri: Uri?) {
        // Generate unique ID and get the current date
        val id = generateLongFromUUID().toInt()
        val date = getCurrentDate()


        val note = NoteModel(id, title, date, selectedChap, selectedClass, selectedSubject, downloadUri.toString())

        val firestore = Firebase.firestore

        try {
            firestore.collection("Class")
                .document(selectedClass)
                .collection("Subjects")
                .document(selectedSubject)
                .collection("Chapters")
                .document(selectedChap)
                .collection("Notes")
                .document(id.toString())
                .set(note)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Data uploaded successfully", Toast.LENGTH_SHORT).show()
                     emptyfeild()
                    // Dismiss the progress dialog on the UI thread after successful Firestore upload
                    activity?.runOnUiThread {
                        ProgressDialogUtil.dismissProgressDialog()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Firestore", "Error uploading data: ${exception.message}")
                    ProgressDialogUtil.dismissProgressDialog()  // Dismiss dialog after error
                    Toast.makeText(requireContext(), "Failed to upload data: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } catch (e: Exception) {
            Log.e("Firestore", "Error uploading data: ${e.message}")
            ProgressDialogUtil.dismissProgressDialog()  // Dismiss dialog after error
        }
    }

    private fun emptyfeild() {
       binding.NoteTitle.text.clear()
       binding.subjectspinnerNote.setSelection(0)
       binding.classspinnerNote.setSelection(0)
       binding.chapterspinnerNote.setSelection(0)
        binding.animationViewNote.setAnimation(R.raw.pdfbefore)
        binding.animationViewNote.playAnimation()
        binding.pdfStatusNote.text="Select Pdf"
    }

    //==============UPLOAD DATA TO FIREBASE=====================


    //===============PDF PIKER INTENT FUNTION====================
    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/pdf"
        }
        filePickerLauncher.launch(Intent.createChooser(intent, "Select PDF"))
    }

    // File picker result launcher
    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                selectedPdfUri = result.data?.data
                binding.animationViewNote.setAnimation(R.raw.pdfupload)
                binding.animationViewNote.playAnimation()
                val name=getFileName(selectedPdfUri)
                binding.pdfStatusNote.text=name
            }
        }

    private fun getFileName(uri: Uri?): String? {
        var fileName: String? = null
        if (uri?.scheme == "content") {
            val cursor: Cursor? = requireActivity().contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    fileName = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (fileName == null) {
            fileName = uri?.path?.substringAfterLast('/')
        }
        return fileName
    }
    //===============PDF PIKER INTENT FUNTION====================
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
}