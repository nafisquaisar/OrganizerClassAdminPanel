package com.example.nafis.nf2024.organizeradminpanel.Fragment

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.nafis.nf2024.organizeradminpanel.MainActivity
import com.example.nafis.nf2024.organizeradminpanel.Model.DoubtModel
import com.example.nafis.nf2024.organizeradminpanel.ProgressDialogUtil
import com.example.nafis.nf2024.organizeradminpanel.R
import com.example.nafis.nf2024.organizeradminpanel.databinding.DoubtItemDialogBinding
import com.example.nafis.nf2024.organizeradminpanel.databinding.FragmentDoubtSolveBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class DoubtSolveFragment : Fragment() {

    private lateinit var binding: FragmentDoubtSolveBinding
    private var uid: String = ""
    private var doubtId: String = ""
    private var userName: String = ""
    private var userEmail: String = ""
    private var userImgUrl: String = ""
    private var studQuesTitle: String = ""
    private var studQuesDesc: String = ""
    private var studQuesImgUrl: String = ""
    private var teachAnsDesc: String = ""
    private var teachAnsImgUrl: Uri? = null
    private lateinit var teachUploadDoubtImg:ImageView
    private var position: Int = 0
    private var timestamp: Long = 0

    private val selectImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            teachAnsImgUrl = uri
            teachUploadDoubtImg.setImageURI(uri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDoubtSolveBinding.inflate(inflater, container, false)
        // Set up toolbar
        MainActivity.binding.backarrow.visibility = View.VISIBLE
        MainActivity.binding.titleName.text = "Doubt Solving Pannel"
        MainActivity.binding.backarrow.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Retrieve arguments
        uid = arguments?.getString("uid").orEmpty()
        doubtId = arguments?.getString("doubtId").orEmpty()
        userName = arguments?.getString("userName").orEmpty()
        userEmail = arguments?.getString("userEmail").orEmpty()
        userImgUrl = arguments?.getString("userImgUrl").orEmpty()
        studQuesTitle = arguments?.getString("studQuesTitle").orEmpty()
        studQuesDesc = arguments?.getString("studQuesDesc").orEmpty()
        studQuesImgUrl = arguments?.getString("studQuesImgUrl").orEmpty()
        teachAnsDesc = arguments?.getString("teachAnsDesc").orEmpty()
        teachAnsImgUrl = arguments?.getString("teachAnsImgUrl")?.toUri()
        position = arguments?.getInt("position") ?: 0
        timestamp = arguments?.getLong("timestamp") ?: 0L

        showAllData()

        binding.solutionSubmitUpdate.setOnClickListener {
            val btn=binding.solutionSubmitUpdate.text.toString()
            showUploadDialog(btn,teachAnsImgUrl)
        }

        binding.studentQuesImg.setOnClickListener {
            showImageDialog(studQuesImgUrl)
        }

        binding.teachAnsImage.setOnClickListener {
            showImageDialog(teachAnsImgUrl.toString())
        }

        return binding.root
    }

    //===============show Image Dialog Image in full size if we want then click on the photo ==================
    private fun showImageDialog(QuesImgUrl: String) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.image_view_dialog)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.show()

        // Set the image in the dialog
        val fullImageView = dialog.findViewById<ImageView>(R.id.fullImageView)
        Glide.with(requireContext())
            .load(QuesImgUrl)
            .placeholder(R.drawable.questionicon)
            .into(fullImageView)
        // Dismiss the dialog when the image is clicked
        fullImageView.setOnClickListener {
            dialog.dismiss()
        }
    }


    // ========show dialog to add new doubt the list=================
    private fun showUploadDialog(btn: String, teachAnsImgUrl: Uri?) {
        val dialog = Dialog(requireContext())
        val dialogBinding = DoubtItemDialogBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.show()

        teachUploadDoubtImg =dialogBinding.teachUploadDoubtPhoto

        if(btn=="Update"){
            Glide.with(requireContext())
                .load(teachAnsImgUrl)
                .placeholder(R.drawable.questionicon)
                .into(teachUploadDoubtImg)
            dialogBinding.textImage.text="Click for Update Image"
            dialogBinding.doubtEditDesc.setText(teachAnsDesc)
        }
        teachUploadDoubtImg.setOnClickListener {
            selectImage.launch("image/*")
        }

        dialogBinding.doubtSubmit.setOnClickListener {
            teachAnsDesc = dialogBinding.doubtEditDesc.text.toString()
            if (teachAnsDesc.isNotBlank() && this.teachAnsImgUrl != null) {
                uploadImage(teachAnsDesc, dialog)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please provide a description and select an image",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        dialogBinding.doubtCancel.setOnClickListener {
            dialog.dismiss()
        }
    }

    //====== upload image to the firebase================
    private fun uploadImage(teachAnsDesc: String, dialog: Dialog) {
        val ref = FirebaseStorage.getInstance().getReference("Doubt").child(uid).child(doubtId)

        val task = ref.putFile(teachAnsImgUrl!!)
        ProgressDialogUtil.showProgressDialog(requireContext(), task)

        task.continueWithTask { ref.downloadUrl }
            .addOnCompleteListener { downloadTask ->
                if (downloadTask.isSuccessful) {
                    val downloadUrl = downloadTask.result.toString()
                    uploadData(teachAnsDesc, downloadUrl, dialog)
                } else {
                    ProgressDialogUtil.dismissProgressDialog()
                    Toast.makeText(
                        requireContext(),
                        "Failed to upload image",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener {
                ProgressDialogUtil.dismissProgressDialog()
                Toast.makeText(
                    requireContext(),
                    "Image upload error: ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


    //========== upload all the data of doubt in the database===============
    private fun uploadData(teachAnsDesc: String, downloadUrl: String, dialog: Dialog) {
        val ref = FirebaseDatabase.getInstance().getReference("Doubt").child(uid).child(doubtId)

        val doubtModel = DoubtModel(
            doubtId, uid, userName, userImgUrl, userEmail,
            studQuesImgUrl, studQuesTitle, studQuesDesc, downloadUrl, teachAnsDesc, timestamp,false,true
        )

        ref.setValue(doubtModel)
            .addOnSuccessListener {
                ProgressDialogUtil.dismissProgressDialog()
                showAllData()
                dialog.dismiss()
                Toast.makeText(requireContext(), "Solution submitted successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                ProgressDialogUtil.dismissProgressDialog()
                Toast.makeText(
                    requireContext(),
                    "Failed to submit solution: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    // =========show all the data doubt int he list================
    private fun showAllData() {
        binding.apply {
            Glide.with(requireContext())
                .load(studQuesImgUrl)
                .placeholder(R.drawable.questionicon)
                .into(studentQuesImg)

            studentQuesTitle.text = studQuesTitle
            studentQuesDesc.text = studQuesDesc
            positionId.text = "${position + 1})"

            if (teachAnsDesc.isNotEmpty() && teachAnsImgUrl != null) {
                llSolution.visibility = View.VISIBLE
                teachAnsDescription.text = teachAnsDesc
                Glide.with(requireContext())
                    .load(teachAnsImgUrl)
                    .placeholder(R.drawable.questionicon)
                    .into(teachAnsImage)
                solutionSubmitUpdate.setText("Update")
            } else {
                llSolution.visibility = View.GONE
            }
        }
    }
}
