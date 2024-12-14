package com.example.nafis.nf2024.organizeradminpanel

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
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
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.example.nafis.nf2024.organizeradminpanel.Adapter.NoteAdapter
import com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack.NoteCallback
import com.example.nafis.nf2024.organizeradminpanel.Model.Note
import com.example.nafis.nf2024.organizeradminpanel.databinding.FragmentNoteViewBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

class NoteFragmentView(var courseId: String?,var courseName:String?,var subjectId: String?,var chapterId: String?) : Fragment() {
    private lateinit var binding:FragmentNoteViewBinding
    private lateinit var noteList :ArrayList<Note>
    private lateinit var noteAdapter:NoteAdapter
    private lateinit var pdfViewer: LottieAnimationView
    private  var selectedPdfUri:Uri?=null
    private var notePdfStatus: TextView? =null
    private var db=Firebase.firestore

    private val callback by lazy {
        object : NoteCallback{
            override fun onNoteClick(item: Note) {
                 openPdf(item.noteUrl)
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun updateNoteClick(item: Note) {
                  addNoteDialog(item,true)
            }

            override fun deleteNoteClick(itemId: String) {
                   deleteNote(itemId)
            }

        }
    }




    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentNoteViewBinding.inflate(inflater,container,false)

        noteList= ArrayList()
        noteAdapter= NoteAdapter(requireContext(),callback,noteList)
        binding.NoteRecyclerView.layoutManager=LinearLayoutManager(requireContext())
        binding.NoteRecyclerView.adapter=noteAdapter

        fetchNote()
        binding.addNote.setOnClickListener {
            addNoteDialog(isUpdate = false)
        }


        return binding.root
    }


    // ===========fetch the note ==============
    private fun fetchNote() {
        var db=Firebase.firestore
         noteList.clear()
        db.collection("courses")
            .document(courseId!!)
            .collection("subjects")
            .document(subjectId!!)
            .collection("Chapters")
            .document(chapterId!!)
            .collection("Notes")
            .get()
            .addOnSuccessListener {Notes->
                for(note in Notes){
                    val Note=note.toObject(Note::class.java)
                    noteList.add(Note)
                }

                if(noteList.isEmpty()){
                    binding.helping.visibility=View.VISIBLE
                } else {
                    noteAdapter.submitList(noteList)
                    binding.helping.visibility=View.GONE
                    noteAdapter.notifyDataSetChanged()
                }
                binding.progressbar.visibility = View.GONE

            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to fetch Notes: ${it.message}", Toast.LENGTH_SHORT).show()
                binding.progressbar.visibility = View.GONE
                binding.helping.visibility=View.VISIBLE
            }
    }


    //===============dialog open for update and upload============
    @RequiresApi(Build.VERSION_CODES.O)
    private fun addNoteDialog(item: Note? = null, isUpdate: Boolean) {
        val dialog=Dialog(requireContext())
        dialog.setContentView(R.layout.add_note_dialog)
        dialog.show()

        val noteNameEdit=dialog.findViewById<EditText>(R.id.dialogNoteTitle)
        notePdfStatus=dialog.findViewById<TextView>(R.id.notePdfStatusNote)
        pdfViewer=dialog.findViewById<LottieAnimationView>(R.id.dialogAnimationViewNote)

        pdfViewer.setOnClickListener {
            openFilePicker()
        }

        val saveNoteBtn=dialog.findViewById<Button>(R.id.dialogUplaodNoteBtn)
        val cancelBtn=dialog.findViewById<Button>(R.id.dialogCancelNoteBtn)

        if(isUpdate && item!=null){
            saveNoteBtn.setText("Update")
            noteNameEdit.setText(item.noteTitle)
            pdfViewer.setAnimation(R.raw.pdfupload)
            pdfViewer.playAnimation()
            notePdfStatus?.text="Update Pdf"

            saveNoteBtn.setOnClickListener {
                val updateNoteTitle=noteNameEdit.text.toString().trim()
                uploadPdf(updateNoteTitle,true,item)
                dialog.dismiss()
            }
        }else{
            saveNoteBtn.setOnClickListener {
                val noteTitle=noteNameEdit.text.toString().trim()
                uploadPdf(noteTitle, false)
                dialog.dismiss()
            }
        }

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }
    }


    //==============pdf upload or update===================
    @RequiresApi(Build.VERSION_CODES.O)
    private fun uploadPdf(noteTitle: String, isUpdate: Boolean, item: Note? = null) {
        val databaseStorage = FirebaseStorage.getInstance()
        val reference = databaseStorage.reference.child("Note/$courseId/$subjectId/$chapterId/$noteTitle")

        if (selectedPdfUri != null) {
            // Upload new PDF if selected
            val uploadTask = reference.putFile(selectedPdfUri!!)
            ProgressDialogUtil.showProgressDialog(requireContext(), uploadTask)

            uploadTask.addOnProgressListener { taskSnapshot ->
                val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
                ProgressDialogUtil.updateProgress(progress)
            }

            uploadTask.addOnSuccessListener {
                reference.downloadUrl.addOnSuccessListener { downloadUri ->
                    uploadUrl(downloadUri, noteTitle,isUpdate =isUpdate,item)
                }
            }.addOnFailureListener { exception ->
                Log.e("Firebase", "Upload failed: ${exception.message}")
                Toast.makeText(requireContext(), "Upload failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                ProgressDialogUtil.dismissProgressDialog()
            }
        } else {
            // No new PDF selected, retain the old URL
            uploadUrl(item?.noteUrl?.let { Uri.parse(it) }, noteTitle, isUpdate, item)
        }
    }


    //=========pdf all detail upload or update================
    @RequiresApi(Build.VERSION_CODES.O)
    private fun uploadUrl(downloadUri: Uri?, noteTitle: String,isUpdate: Boolean,item:Note?=null) {

        // Generate unique ID and get the current date
        val firestore = Firebase.firestore
        if(isUpdate && item!=null){
            val note = hashMapOf(
                "noteId" to item.noteId,
                "noteTitle" to noteTitle,
                "noteUrl" to  downloadUri,
                "date"  to item.date
            )
            try {
                firestore.collection("courses")
                    .document(courseId!!)
                    .collection("subjects")
                    .document(subjectId!!)
                    .collection("Chapters")
                    .document(chapterId!!)
                    .collection("Notes")
                    .document(item.noteId)
                    .update(note as Map<String,Any>)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Note Update successfully", Toast.LENGTH_SHORT).show()
                        activity?.runOnUiThread {
                            ProgressDialogUtil.dismissProgressDialog()
                        }
                        fetchNote()
                    }
                    .addOnFailureListener { exception ->
                        Log.e("Firestore", "Error updating data: ${exception.message}")
                        ProgressDialogUtil.dismissProgressDialog()  // Dismiss dialog after error
                        Toast.makeText(requireContext(), "Failed to update data: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            } catch (e: Exception) {
                Log.e("Firestore", "Error updating data: ${e.message}")
                ProgressDialogUtil.dismissProgressDialog()  // Dismiss dialog after error
            }

        }else{
            val id = generateLongFromUUID(noteTitle)
            val date = getCurrentDate()
            val note = hashMapOf(
                "noteId" to id,
                "noteTitle" to noteTitle,
                "noteUrl" to  downloadUri,
                "date"  to date
            )
            try {
                firestore.collection("courses")
                    .document(courseId!!)
                    .collection("subjects")
                    .document(subjectId!!)
                    .collection("Chapters")
                    .document(chapterId!!)
                    .collection("Notes")
                    .document(id)
                    .set(note)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Data uploaded successfully", Toast.LENGTH_SHORT).show()
                        // Dismiss the progress dialog on the UI thread after successful Firestore upload
                        activity?.runOnUiThread {
                            ProgressDialogUtil.dismissProgressDialog()
                        }
                        fetchNote()
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
    }


    //================ file piker and get the Uri of file==================
    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                selectedPdfUri = result.data?.data!!
                pdfViewer.setAnimation(R.raw.pdfupload)
                pdfViewer.playAnimation()
                val name=getFileName(selectedPdfUri)
                notePdfStatus?.text=name
            }
        }

    //===============PDF PIKER INTENT FUNTION====================
    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/pdf"
        }
        filePickerLauncher.launch(Intent.createChooser(intent, "Select PDF"))
    }

    // ===========get the file name from the galery==========
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDate(): String {
        val currentDate = LocalDate.now()
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return currentDate.format(dateFormatter)
    }


    //==============Delete the Funtion==============
    private fun deleteNote(itemId: String) {
        val chapterRef =   db.collection("courses")
            .document(courseId!!)
            .collection("subjects")
            .document(subjectId!!)
            .collection("Chapters")
            .document(chapterId!!)
            .collection("Notes")
            .document(itemId)

        AlertDialog.Builder(requireContext())
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete this note?")
            .setPositiveButton("Yes") { _, _ ->
                chapterRef.delete()
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "note deleted successfully", Toast.LENGTH_SHORT).show()
                        fetchNote() // Refresh the list
                        noteAdapter.notifyDataSetChanged() // .notifyDataSetChanged()
                    }
                    .addOnFailureListener { error ->
                        Toast.makeText(requireContext(), "Failed to delete: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("No", null)
            .show()
    }

    //=======open Pdf Funtion===========
    fun openPdf(pdfUrl: String? ){
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.parse(pdfUrl), "application/pdf")
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Handle the case where no PDF reader is installed
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            Log.d("PDF" ,e.message.toString())
        }
    }

    fun generateLongFromUUID(note:String): String {
        val sanatizedNoteName=note.replace(Regex("[^a-zA-Z0-9]"),"")
        return "${sanatizedNoteName}-${UUID.randomUUID()}"
    }


}