package com.example.nafis.nf2024.organizeradminpanel.Fragment

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.example.nafis.nf2024.organizeradminpanel.MainActivity
import com.example.nafis.nf2024.organizeradminpanel.Model.NoteModel
import com.example.nafis.nf2024.organizeradminpanel.Obj
import com.example.nafis.nf2024.organizeradminpanel.ProgressDialogUtil
import com.example.nafis.nf2024.organizeradminpanel.R
import com.example.nafis.nf2024.organizeradminpanel.databinding.FragmentNoteBinding
import com.example.nafis.nf2024.organizeradminpanel.databinding.FragmentPYQBinding
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

class PYQFragment : Fragment() {
    private lateinit var binding: FragmentPYQBinding
    private var selectedClass = ""
    private var selectedSubject = ""
    private var selectedChap = ""
    private var selectedBoard = ""
    private var title = ""
    private var selectedPdfUri: Uri? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPYQBinding.inflate(inflater, container, false)


        MainActivity.binding.backarrow.visibility = View.VISIBLE
        MainActivity.binding.titleName.text = "PYQ Upload Pannel"
        MainActivity.binding.backarrow.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val classes = Obj.classes()

        // Set up the class spinner
        setupClassSpinner(classes)
        setUpBoardType()
        binding.uplaodPYQBtn.setOnClickListener {
            uploadData()
        }

        binding.animationViewPyq.setOnClickListener {
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
        binding.classspinnerpdf.adapter = adapter

        // Handle class selection
        binding.classspinnerpdf.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
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
        binding.subjectspinnerpdf.adapter = adapter

        // Handle subject selection
        binding.subjectspinnerpdf.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedSubject = parent.getItemAtPosition(position).toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Handle no selection
                }
            }
    }
    //==============SUBJECT SPINNER=====================



    //==============TYPE SPINNER=====================
    fun setUpBoardType() {

        val types = Obj.Board()
        var adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            types
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.typespinnerpdf.adapter = adapter
        binding.typespinnerpdf.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    selectedBoard = p0?.getItemAtPosition(p2).toString()
                    if (p2 != 0) {
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

            }
    }
    //==============TYPE SPINNER=====================

    //==============UPLOAD DATA TO FIREBASE=====================
    @RequiresApi(Build.VERSION_CODES.O)
    private fun uploadData() {
        // Get the title and generate the file name
        title = binding.pdfTitle.text.toString()


        if (title.isEmpty() ||  selectedClass.isEmpty()&& selectedClass=="Select Class" ||
            selectedSubject.isEmpty() || selectedSubject=="Select Subject"|| selectedBoard.isEmpty() ||
            selectedBoard=="Select Board"){
            Toast.makeText(requireContext(), "Feild cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate file selection
        if (selectedPdfUri == null) {
            Toast.makeText(requireContext(), "Pick the Note", Toast.LENGTH_SHORT).show()
            return
        }


        val first10Characters = title.take(10) // Take first 10 characters of the title
        val randomNumber = (10000..99999).random() // Generate a random 5-digit number
        val fileName = "$first10Characters$randomNumber.pdf" // Combine them to create the file name

        // Upload PDF to Firebase Storage

        val storage = FirebaseStorage.getInstance()
        val storageRef: StorageReference = storage.reference.child("PYQ/$selectedClass/$selectedSubject/$fileName")

        val uploadTask = selectedPdfUri?.let { storageRef.putFile(it) }

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
               storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
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
        if (downloadUri == null) {
            Toast.makeText(requireContext(), "Failed to get download URL", Toast.LENGTH_SHORT).show()
            return
        }

        // Generate a unique ID (UUID) for the document
        val uniqueId = generateLongFromUUID().toInt()// Generate a unique ID
        val date = getCurrentDate()


        val data=NoteModel(uniqueId,title,date,selectedChap,selectedClass,selectedSubject,downloadUri.toString())


        // Reference to the Firestore collection
        val firestore = Firebase.firestore

        try {
            firestore.collection("Class")
                .document(selectedClass)
                .collection("Subjects")
                .document(selectedSubject)
                .collection("Board")
                .document(selectedBoard)
                .collection("PYQ") // Sub collection for storing PYQs
                .document(uniqueId.toString()) // Using custom document ID (uniqueId)
                .set(data) // Set the document with the custom ID
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Data uploaded successfully", Toast.LENGTH_SHORT).show()
                    activity?.runOnUiThread {
                        emptyfeild()
                        ProgressDialogUtil.dismissProgressDialog()

                    }
                }
                .addOnFailureListener { exception ->
                    ProgressDialogUtil.dismissProgressDialog()  // Dismiss dialog after error
                    Toast.makeText(requireContext(), "Failed to upload data: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }catch (e:Exception){
            ProgressDialogUtil.dismissProgressDialog()  // Dismiss dialog after error
            Toast.makeText(requireContext(), "Failed to upload data: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    //==============UPLOAD DATA TO FIREBASE=====================
    private fun emptyfeild() {
        binding.pdfTitle.text.clear()
        binding.subjectspinnerpdf.setSelection(0)
        binding.classspinnerpdf.setSelection(0)
        binding.typespinnerpdf.setSelection(0)
        binding.animationViewPyq.setAnimation(R.raw.pdfbefore)
        binding.animationViewPyq.playAnimation()
        binding.pdfStatuspyq.text="Select Pdf"
    }

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
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                selectedPdfUri = result.data?.data
                binding.animationViewPyq.setAnimation(R.raw.pdfupload)
                binding.animationViewPyq.playAnimation()
                val name = getFileName(selectedPdfUri)
                binding.pdfStatuspyq.text = name
            }
        }

    private fun getFileName(uri: Uri?): String? {
        var fileName: String? = null
        if (uri?.scheme == "content") {
            val cursor: Cursor? =
                requireActivity().contentResolver.query(uri, null, null, null, null)
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
    fun generateLongFromUUID(): Long {
        val uuid = UUID.randomUUID()
        return uuid.leastSignificantBits // Extract least significant bits
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDate(): String {
        val currentDate = LocalDate.now()
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return currentDate.format(dateFormatter)
    }

}