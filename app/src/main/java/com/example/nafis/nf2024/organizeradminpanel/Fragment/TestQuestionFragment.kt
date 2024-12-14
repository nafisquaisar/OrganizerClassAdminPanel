package com.example.nafis.nf2024.organizeradminpanel.Fragment

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nafis.nf2024.organizeradminpanel.Adapter.QuestionListAdapter
import com.example.nafis.nf2024.organizeradminpanel.MainActivity
import com.example.nafis.nf2024.organizeradminpanel.Model.QuizModel
import com.example.nafis.nf2024.organizeradminpanel.Model.QuizOption
import com.example.nafis.nf2024.organizeradminpanel.Model.TestObject
import com.example.nafis.nf2024.organizeradminpanel.ProgressDialogUtil
import com.example.nafis.nf2024.organizeradminpanel.R
import com.example.nafis.nf2024.organizeradminpanel.databinding.FragmentTestQuestionBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class TestQuestionFragment : Fragment() {
    private lateinit var binding: FragmentTestQuestionBinding
//    private lateinit var quizOp:ArrayList<QuizOption>
    private lateinit var quizQues:ArrayList<QuizModel>
    private lateinit var quizs: TestObject
    private lateinit var ques: QuizModel
    private var id=0
    private var testId:String=""
    private lateinit var adapter:QuestionListAdapter
    private var imgUri: Uri? = null
    private lateinit var imgDailog: ImageView
    private lateinit var  title:TextView
    private lateinit var testtitle:String
    private lateinit var className:String
    private lateinit var subjectName:String
    private lateinit var chapterName:String
    private lateinit var questionCount:String


    companion object{
        var fileName: String? =null
        // Companion object to store the ActivityResultLauncher
//        lateinit var imageselected: ActivityResultLauncher<String>
    }
   private var imageselected=registerForActivityResult(ActivityResultContracts.GetContent()){
        imgUri=it
        imgDailog.setImageURI(imgUri)
        title.text= imgUri?.let { it1 -> getFileNameFromUri(it1) }

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTestQuestionBinding.inflate(inflater, container, false)



        MainActivity.binding.backarrow.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }


        quizQues=ArrayList()
         ques= QuizModel()
        // Retrieve data from the bundle
        testtitle = arguments?.getString("argTitle").toString()
        val totalMark = arguments?.getString("argTotalMark")
        questionCount = arguments?.getString("argQuestionCount").toString()
        className = arguments?.getString("argClassName").toString()
        subjectName = arguments?.getString("argSubjectName").toString()
        chapterName = arguments?.getString("argChapterName").toString()
        val timeDuration = arguments?.getString("argTimeDuration").toString()
        val time= timeDuration.split(" ").firstOrNull() ?: ""
        // Set the FAB click listener to show the dialog


            binding.fab.setOnClickListener {
                if(questionCount.toInt()==quizQues.size){
                    Toast.makeText(requireContext(),"All Question are added",Toast.LENGTH_SHORT).show()
                }else{
                 imgUri=null
                  fileName=null
                dialogBox()
               }
            }

        testId=UUID.randomUUID().toString()
        quizs=TestObject(testId,testtitle,questionCount.toString(),
                                           totalMark.toString(),time,className,
                                           subjectName,chapterName,
                                           listOf(ques)
        )

        binding.uploadBtn.setOnClickListener {
            when {
                quizQues.isEmpty() -> {
                    Toast.makeText(requireContext(), "No questions added. Please add questions to upload.", Toast.LENGTH_SHORT).show()
                }
                quizQues.size < questionCount?.toInt() ?: 0 -> {
                    val remaining = (questionCount?.toInt() ?: 0) - quizQues.size
                    Toast.makeText(requireContext(), "Add $remaining more questions.", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    uploadQuestion(className, subjectName, chapterName)
                }
            }
        }



        showQuestion()
        return binding.root
    }

    private fun uploadQuestion(classname: String, subjectName: String, chapter: String) {
        // Update the question list in quizs
        ProgressDialogUtil.showProgress(requireContext(),0)
        quizs.questions = quizQues

      try {
          // Upload the TestObject to Firebase
          val db = FirebaseDatabase.getInstance()
          val ref = db.getReference("Class").child(classname)
              .child(subjectName).child(chapter).child(testId)

          ref.setValue(quizs)
              .addOnSuccessListener {
                  Log.d("FirebaseUpload", "Question uploaded successfully!")
                  Toast.makeText(requireContext(), "Upload successful", Toast.LENGTH_SHORT).show()
                  quizQues.clear()
                  showQuestion()
                  ProgressDialogUtil.dismissProgressDialog()
              }
              .addOnFailureListener { e ->
                  Log.e("FirebaseUpload", "Failed to upload question: ${e.message}")
                  Toast.makeText(requireContext(), "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
                  ProgressDialogUtil.dismissProgressDialog()
              }
      }catch (e:Exception){
          Log.e("FirebaseUpload", "Failed to upload question: ${e.message}")
          Toast.makeText(requireContext(), "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
          ProgressDialogUtil.dismissProgressDialog()
      }
    }

    private fun showQuestion() {
        binding.recyclerView.layoutManager=LinearLayoutManager(requireContext())
        binding.recyclerView.setHasFixedSize(true)
        adapter= QuestionListAdapter(requireContext(),quizQues,this,::onQuestionUpdated)
        binding.recyclerView.adapter=adapter
        attachSwipeToDelete(binding.recyclerView,adapter)
    }

    private fun dialogBox() {
        val dialog = Dialog(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.question_dailog, null)
        dialog.setContentView(dialogView)

        dialog.window?.apply {
            val params = attributes
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            attributes = params
        }

        val question = dialogView.findViewById<EditText>(R.id.testQuestion)
        val option1 = dialogView.findViewById<EditText>(R.id.testOption1)
        val option2 = dialogView.findViewById<EditText>(R.id.testOption2)
        val option3 = dialogView.findViewById<EditText>(R.id.testOption3)
        val option4 = dialogView.findViewById<EditText>(R.id.testOption4)
        val correctOp = dialogView.findViewById<EditText>(R.id.correctOption)
        val btn = dialogView.findViewById<Button>(R.id.addBtn)
        val cancel = dialogView.findViewById<Button>(R.id.cancelBtn)
        imgDailog = dialogView.findViewById(R.id.question_img)
        title = dialogView.findViewById(R.id.question_img_text)

        imgDailog.setOnClickListener {
           launchImagePicker()
        }


            btn.setOnClickListener {
                val questionText = question.text.toString()
                val option1Text = option1.text.toString()
                val option2Text = option2.text.toString()
                val option3Text = option3.text.toString()
                val option4Text = option4.text.toString()
                val correctOpText = correctOp.text.toString()

                if (questionText.isBlank() || correctOpText.isBlank() || option1Text.isBlank() ||
                    option2Text.isBlank() || option3Text.isBlank() || option4Text.isBlank()) {
                    Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Upload photo and add question after upload
                uploadPhoto { imageUrl ->
                    if (imageUrl != null) {
                        val optionList = QuizOption(option1Text, option2Text, option3Text, option4Text)
                        val ques = QuizModel(
                            (++id).toString(),
                            questionText,
                            correctOpText,
                            imageUrl,
                            listOf(optionList)
                        )
                        quizQues.add(ques)
                        adapter.notifyItemInserted(quizQues.size - 1)
                        dialog.dismiss()
                    } else {
                        val optionList = QuizOption(option1Text, option2Text, option3Text, option4Text)
                        val ques = QuizModel(
                            (++id).toString(),
                            questionText,
                            correctOpText,
                            "",
                            listOf(optionList)
                        )
                        quizQues.add(ques)
                        adapter.notifyItemInserted(quizQues.size - 1)
                        dialog.dismiss()
                    }
                }
            }


        cancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun uploadPhoto(onComplete: (String?) -> Unit) {
        if (imgUri == null) {
            onComplete(null)
            return
        }
        ProgressDialogUtil.showProgress(requireContext(),0)

        val firebase = FirebaseStorage.getInstance()
        val ref = firebase.getReference("Test")
            .child(className)
            .child(subjectName)
            .child(chapterName)
            .child(testtitle)
            .child("$title.Png")

        ref.putFile(imgUri!!)
            .addOnSuccessListener {
                // Retrieve the download URL after a successful upload
                ref.downloadUrl.addOnSuccessListener { uri ->
                    onComplete(uri.toString()) // Pass the URL back via the callback
                    ProgressDialogUtil.dismissProgressDialog()
                }.addOnFailureListener { e ->
                    Log.e("FirebaseUpload", "Failed to get download URL: ${e.message}")
                    onComplete(null) // Pass null in case of failure
                    ProgressDialogUtil.dismissProgressDialog()
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseUpload", "Image upload failed: ${e.message}")
                    ProgressDialogUtil.dismissProgressDialog()
                onComplete(null) // Pass null in case of failure
            }
    }

    private fun onQuestionUpdated(updatedQuestion: QuizModel) {
        // Find the index of the updated question
        val index = quizQues.indexOfFirst { it.quizId == updatedQuestion.quizId }

        if (index != -1) {
            // Update the item in the list
            quizQues[index] = updatedQuestion

            // Notify the adapter about the change
            adapter.notifyItemChanged(index)

            // Optional: Log or handle further updates
            Log.d("QuizFragment", "Question updated: $updatedQuestion")
        } else {
            Log.e("QuizFragment", "Question not found for update!")
        }
    }

    private fun attachSwipeToDelete(recyclerView: RecyclerView, adapter: QuestionListAdapter) {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false // No move support needed
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val builder = AlertDialog.Builder(recyclerView.context)
                builder.setTitle("Delete Question")
                    .setMessage("Are you sure you want to delete this question?")
                    .setPositiveButton("Yes") { dialog, _ ->
                        // Proceed with deletion
                        adapter.removeItem(position)
                        dialog.dismiss()
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        // Cancel deletion and reset the swiped item
                        adapter.notifyItemChanged(position)
                        dialog.dismiss()
                    }
                    .setCancelable(false)
                    .show()
            }

            override fun onChildDraw(
                c: android.graphics.Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val backgroundPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.RED
                }

                // Draw background color based on swipe direction
                if (dX > 0) {
                    // Swiping right
                    c.drawRect(
                        itemView.left.toFloat(),
                        itemView.top.toFloat(),
                        itemView.left + dX,
                        itemView.bottom.toFloat(),
                        backgroundPaint
                    )
                } else if (dX < 0) {
                    // Swiping left
                    c.drawRect(
                        itemView.right + dX,
                        itemView.top.toFloat(),
                        itemView.right.toFloat(),
                        itemView.bottom.toFloat(),
                        backgroundPaint
                    )
                }

                // Call the default swipe behavior
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        })

        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.updateToolbar("Question Panel", true)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        MainActivity.binding.titleName.setText("Video Upload Pannel")
    }

    // Function to get the file name from a Uri
// Function to get the file name from a Uri
    private fun getFileNameFromUri(uri: Uri): String? {

        if (uri.scheme == "content") {
            // Query the ContentResolver for the file name
            val cursor = requireActivity().contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    fileName = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (fileName == null) {
            // If the scheme is not "content", extract the file name from the URI path
            fileName = uri.path?.substringAfterLast('/').toString()
        }
        return fileName
    }

    fun UpdateDilogshowDialog(model: QuizModel, position: Int, imgUrl: String) {
        // Create the dialog
        val dialog = Dialog(requireContext())
        val dialogView = LayoutInflater.from(context).inflate(R.layout.question_dailog, null)
        dialog.setContentView(dialogView)

        // Set dialog width and height
        val window = dialog.window
        window?.apply {
            val params = attributes
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            attributes = params
        }

        // Initialize dialog components
        val question = dialogView.findViewById<EditText>(R.id.testQuestion)
        val option1 = dialogView.findViewById<EditText>(R.id.testOption1)
        val option2 = dialogView.findViewById<EditText>(R.id.testOption2)
        val option3 = dialogView.findViewById<EditText>(R.id.testOption3)
        val option4 = dialogView.findViewById<EditText>(R.id.testOption4)
        val correctOp = dialogView.findViewById<EditText>(R.id.correctOption)
        val updatebtn = dialogView.findViewById<Button>(R.id.addBtn)
        val cancel = dialogView.findViewById<Button>(R.id.cancelBtn)
        imgDailog = dialogView.findViewById(R.id.question_img)
        var updatetitle = dialogView.findViewById<TextView>(R.id.question_img_text)
        updatebtn.setText("Update")
        updatetitle.text="Update Question (if needed)"
        // Trigger the image selection when the image view is clicked
        imgDailog.setOnClickListener {
            launchImagePicker()
        }

        // If imgUrl is not empty, load the image into the image view using Glide
        if (imgUrl.isNotEmpty()) {
            Glide.with(requireContext())
                .load(imgUrl)
                .placeholder(R.drawable.pyq_icon)
                .into(imgDailog)
        }

        // Populate fields with existing data
        question.setText(model.quizQues)
        val options = model.options.firstOrNull()
        option1.setText(options?.quizOp1)
        option2.setText(options?.quizOp2)
        option3.setText(options?.quizOp3)
        option4.setText(options?.quizOp4)
        correctOp.setText(model.correctOp)

        // Save button click
        updatebtn.setOnClickListener {
            val updatedQuestion = question.text.toString().trim()
            val updatedOption1 = option1.text.toString().trim()
            val updatedOption2 = option2.text.toString().trim()
            val updatedOption3 = option3.text.toString().trim()
            val updatedOption4 = option4.text.toString().trim()
            val updatedCorrectOp = correctOp.text.toString().trim()

            if (updatedQuestion.isEmpty() || updatedOption1.isEmpty() || updatedOption2.isEmpty() ||
                updatedOption3.isEmpty() || updatedOption4.isEmpty() || updatedCorrectOp.isEmpty()
            ) {
                Log.e("Dialog", "Validation failed: All fields are required.")
                return@setOnClickListener
            }

            // Upload photo if necessary
            uploadPhoto { uploadedImgUrl ->
                val finalImgUrl = uploadedImgUrl ?: imgUrl // Use the existing imgUrl if no new image is uploaded

                // Update the model
                val updatedOptions = QuizOption(updatedOption1, updatedOption2, updatedOption3, updatedOption4)
                val updatedModel = QuizModel(
                    model.quizId,
                    updatedQuestion,
                    updatedCorrectOp,
                    finalImgUrl,
                    listOf(updatedOptions)
                )

                // Update list and notify
                quizQues[position] = updatedModel
                adapter.notifyItemChanged(position)

                // Trigger callback for external updates
                onQuestionUpdated(updatedModel)

                // Dismiss dialog
                dialog.dismiss()
            }
        }

        cancel.setOnClickListener {
            dialog.dismiss()
        }

        // Show the dialog
        dialog.show()
    }

    fun launchImagePicker() {
        imageselected.launch("image/*")
    }

}
