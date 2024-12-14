package com.example.nafis.nf2024.organizeradminpanel.Fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.nafis.nf2024.organizeradminpanel.Adapter.CourseAdapter
import com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack.CouseCallback
import com.example.nafis.nf2024.organizeradminpanel.MainActivity
import com.example.nafis.nf2024.organizeradminpanel.Model.CourseModel
import com.example.nafis.nf2024.organizeradminpanel.Model.Subject
import com.example.nafis.nf2024.organizeradminpanel.ProgressDialogUtil
import com.example.nafis.nf2024.organizeradminpanel.R

import com.example.nafis.nf2024.organizeradminpanel.databinding.FragmentCourseBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class CourseFragment : Fragment() {
    private lateinit var binding: FragmentCourseBinding
    private lateinit var courseList: ArrayList<CourseModel>
    private lateinit var adapter: CourseAdapter
    private val db = FirebaseFirestore.getInstance()
    private lateinit var imgUri:Uri
    private lateinit  var img: ImageView
    private val callback by lazy {
        object :CouseCallback{
            override fun onCourseClick(item: CourseModel) {
                val bundle = Bundle().apply {
                    putString("courseId", item.courseId)
                    putString("courseName", item.courseName)
                }
                findNavController().navigate(R.id.action_courseFragment_to_courseClassAndTestFragment, bundle)
            }

            override fun onCourseUpdate(item: CourseModel) {
                  updateCourse(item)
            }

            override fun onCourseDelete(itemId: String) {
                 deleteCourse(itemId)
            }

            override fun onCourseDisable(itemId: String) {
                disableCourse(itemId)
            }
        }
    }

    private fun disableCourse(itemId: String) {
        // Find the selected course from the list
        val selectedCourse = courseList.find { it.courseId == itemId }
        if (selectedCourse != null) {
            // Toggle the 'isCourseDisable' status
            val newStatus = !selectedCourse.isCourseDisable

            // Update the course in Firestore
            db.collection("courses").document(itemId)
                .update("isCourseDisable", newStatus)
                .addOnSuccessListener {
                    // Update the local courseList and notify the adapter
                    selectedCourse.isCourseDisable = newStatus
                    adapter.notifyDataSetChanged()

                    // Show a confirmation message
                    val status = if (newStatus) "disabled" else "enabled"
                    Toast.makeText(requireContext(), "Course $status successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    // Show an error message
                    Toast.makeText(requireContext(), "Failed to update course status", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "Course not found", Toast.LENGTH_SHORT).show()
        }
    }


    private fun updateCourse(item: CourseModel) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.course_dailog, null)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val etCourseName = dialogView.findViewById<EditText>(R.id.DialogCourseTitle)
        val etCourseDesc = dialogView.findViewById<EditText>(R.id.DialogCourseDes)
        val etCourseAmount = dialogView.findViewById<EditText>(R.id.DialogCoursePrice)
        val etOfferCourseAmount = dialogView.findViewById<EditText>(R.id.DialogOfferCoursePrice)
        val imgName = dialogView.findViewById<TextView>(R.id.course_img_text)
        img = dialogView.findViewById<ImageView>(R.id.DialogCourse_img)
        val btnAddCourse = dialogView.findViewById<Button>(R.id.addBtn)
        val btnCancelCourse = dialogView.findViewById<Button>(R.id.cancelBtn)

        // Pre-fill the fields with the existing course data
        etCourseName.setText(item.courseName)
        etCourseDesc.setText(item.courseDesc)
        etCourseAmount.setText(item.courseAmount)
        etOfferCourseAmount.setText(item.offerAmount)
        imgName.text = "Update Image (if you want)"
        btnAddCourse.text = "Update"

        // Load the existing image using Glide
        Glide.with(requireContext())
            .load(item.courseImgUrl)
            .into(img)

        img?.setOnClickListener {
            imageSelect.launch("image/*")
        }

        btnAddCourse.setOnClickListener {
            val updatedCourseName = etCourseName.text.toString().trim()
            val updatedCourseDesc = etCourseDesc.text.toString().trim()
            val updatedCourseAmount = etCourseAmount.text.toString().trim()
            val updatedOfferCourseAmount = etOfferCourseAmount.text.toString().trim()

            if (updatedCourseName.isEmpty() || updatedCourseDesc.isEmpty() || updatedCourseAmount.isEmpty() || updatedOfferCourseAmount.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (::imgUri.isInitialized) {
                // If a new image is selected, upload it
                uploadUpdatedImg(item, updatedCourseName, updatedCourseDesc, updatedCourseAmount,updatedOfferCourseAmount)
            } else {
                // Update data without changing the image
                updateCourseData(item, updatedCourseName, updatedCourseDesc, updatedCourseAmount, item.courseImgUrl,updatedOfferCourseAmount)
            }

            dialog.dismiss()
        }

        btnCancelCourse.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun uploadUpdatedImg(
        item: CourseModel,
        updatedCourseName: String,
        updatedCourseDesc: String,
        updatedCourseAmount: String,
        updatedOfferCourseAmount: String
    ) {
        if (imgUri == null) {
            Toast.makeText(requireContext(), "Image not selected. Please select an image.", Toast.LENGTH_SHORT).show()
            return
        }

        val ref = FirebaseStorage.getInstance().getReference("Course").child(updatedCourseName)
        val uploadTask = ref.putFile(imgUri!!)

        ProgressDialogUtil.showProgressDialog(requireContext(), uploadTask) // Show the progress dialog

        uploadTask
            .addOnSuccessListener {
                ref.downloadUrl
                    .addOnSuccessListener { uri ->
                        val newImgUrl = uri.toString()
                        updateCourseData(
                            item,
                            updatedCourseName,
                            updatedCourseDesc,
                            updatedCourseAmount,
                            newImgUrl,
                            updatedOfferCourseAmount
                        )

                        activity?.runOnUiThread {
                            ProgressDialogUtil.dismissProgressDialog()
                        }
                    }
                    .addOnFailureListener { e ->
                        activity?.runOnUiThread {
                            ProgressDialogUtil.dismissProgressDialog()
                        }
                        Toast.makeText(requireContext(), "Failed to get URL: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                activity?.runOnUiThread {
                    ProgressDialogUtil.dismissProgressDialog()
                }
                Toast.makeText(requireContext(), "Image upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
            .addOnCompleteListener {
                // Ensure the dialog is dismissed after the operation is complete
                activity?.runOnUiThread {
                    ProgressDialogUtil.dismissProgressDialog()
                }
            }
    }


    private fun updateCourseData(
        item: CourseModel,
        updatedCourseName: String,
        updatedCourseDesc: String,
        updatedCourseAmount: String,
        updatedCourseImgUrl: String,
        updatedOfferCourseAmount: String
    ) {
        val updatedCourse = hashMapOf(
            "courseId" to item.courseId,
            "courseName" to updatedCourseName,
            "courseDesc" to updatedCourseDesc,
            "courseAmount" to updatedCourseAmount,
            "offerAmount" to updatedOfferCourseAmount,
            "courseImgUrl" to updatedCourseImgUrl,
            "isCourseDisable" to false,
            "courseDate" to item.courseDate
        )

        db.collection("courses").document(item.courseId)
            .update(updatedCourse as Map<String, Any>)
            .addOnSuccessListener {
                fetchCourses() // Refresh the course list
                Toast.makeText(requireContext(), "Course updated successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to update course: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun deleteCourse(itemId: String) {
        val courseRef = db.collection("courses")
            .document(itemId)

        AlertDialog.Builder(requireContext())
            .setTitle("Delete Course")
            .setMessage("Are you sure you want to delete this course?")
            .setPositiveButton("Yes") { _, _ ->
                courseRef.delete()
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Course deleted successfully", Toast.LENGTH_SHORT).show()
                        fetchCourses() // Refresh the list
                        adapter.notifyDataSetChanged()
                    }
                    .addOnFailureListener { error ->
                        Toast.makeText(requireContext(), "Failed to delete: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("No", null)
            .show()
    }

    private val imageSelect = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            imgUri = uri
            img!!.setImageURI(imgUri) // Update the ImageView with the selected image
            Toast.makeText(requireContext(), "Image selected successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCourseBinding.inflate(inflater, container, false)
        // Set up toolbar
        MainActivity.binding.backarrow.visibility = View.VISIBLE
        MainActivity.binding.titleName.text = "Courses"
        MainActivity.binding.backarrow.setOnClickListener {
            MainActivity.binding.backarrow.visibility = View.GONE
            MainActivity.binding.titleName.setText(R.string.organizer_admin_panel)
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Use navigateUp to go back in the navigation stack
                findNavController().navigateUp()

                // You can also hide the back arrow and update the title here
                MainActivity.binding.backarrow.visibility = View.GONE
                MainActivity.binding.titleName.setText(R.string.organizer_admin_panel)
            }
        })

        // FloatingActionButton click listener
        binding.addCourse.setOnClickListener {
            showAddCourseDialog()
        }

        // RecyclerView setup
        courseList = ArrayList()
        adapter = CourseAdapter(requireContext(), callback, courseList)
        binding.CourseRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.CourseRecyclerView.adapter = adapter

        fetchCourses()

        return binding.root
    }

    private fun showAddCourseDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.course_dailog, null)

        val dialog =
            AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val etCourseName = dialogView.findViewById<EditText>(R.id.DialogCourseTitle)
        val etCourseDesc = dialogView.findViewById<EditText>(R.id.DialogCourseDes)
        val etCourseAmount = dialogView.findViewById<EditText>(R.id.DialogCoursePrice)
        val etOfferCourseAmount = dialogView.findViewById<EditText>(R.id.DialogOfferCoursePrice)

        img= dialogView.findViewById<ImageView>(R.id.DialogCourse_img)
        val btnAddCourse = dialogView.findViewById<Button>(R.id.addBtn)
        val btnCancelCourse = dialogView.findViewById<Button>(R.id.cancelBtn)

        img?.setOnClickListener {
            imageSelect.launch("image/*")
        }


        btnAddCourse.setOnClickListener {
            val courseName = etCourseName.text.toString().trim()
            val courseDesc = etCourseDesc.text.toString().trim()
            val courseAmount = etCourseAmount.text.toString().trim()
            val offerCourseAmount = etOfferCourseAmount.text.toString().trim()


            if (imgUri == null) {
                Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (courseName.isNotEmpty() && courseDesc.isNotEmpty() && courseAmount.isNotEmpty() && offerCourseAmount.isNotEmpty()) {
                uploadImg(courseName, courseDesc, courseAmount,offerCourseAmount)

                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Please fill all details", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancelCourse.setOnClickListener {
            dialog.cancel()
        }

        dialog.show()
    }

    private fun uploadImg(
        courseName: String,
        courseDesc: String,
        courseAmount: String,
        offerCourseAmount: String
    ) {
        val ref = FirebaseStorage.getInstance().getReference("Course").child(courseName)

        val uploadTask = imgUri?.let { ref.putFile(it) }
        if (uploadTask == null) {
            Toast.makeText(requireContext(), "Image URI is null", Toast.LENGTH_SHORT).show()
            return
        }

        // Show progress dialog
        ProgressDialogUtil.showProgressDialog(requireContext(), uploadTask)

        uploadTask.addOnProgressListener { snapshot ->
            // Progress updates are handled in ProgressDialogUtil
        }.addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener { uri ->
                val url = uri.toString()
                uploadData(courseName, courseDesc, courseAmount, url,offerCourseAmount)
                activity?.runOnUiThread {
                    ProgressDialogUtil.dismissProgressDialog()
                }
            }.addOnFailureListener { e ->
                activity?.runOnUiThread {
                    ProgressDialogUtil.dismissProgressDialog()
                    Toast.makeText(requireContext(), "Failed to get URL: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }.addOnFailureListener { e ->
            activity?.runOnUiThread {
                ProgressDialogUtil.dismissProgressDialog()
                Toast.makeText(requireContext(), "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadData(
        courseName: String,
        courseDesc: String,
        courseAmount: String,
        url: String,
        offerCourseAmount: String
    ) {
        if (courseName.isNotEmpty() && courseDesc.isNotEmpty() && courseAmount.isNotEmpty() && offerCourseAmount.isNotEmpty()) {
            if (url.isEmpty()) {
                Toast.makeText(requireContext(), "Image upload failed. Try again.", Toast.LENGTH_SHORT).show()
                return
            }

            var id=getCourseId(courseName)
            // Create a course map
            val course = hashMapOf(
                "courseId" to id,
                "courseName" to courseName,
                "courseDesc" to courseDesc,
                "courseAmount" to courseAmount,
                "offerAmount" to offerCourseAmount,
                "courseImgUrl" to url,
                "isCourseDisable" to false,
                "courseDate" to com.google.firebase.Timestamp.now() // Firestore Timestamp for current time
            )

            // Set the courseId to be the same as courseName
            db.collection("courses").document(id).set(course)
                .addOnSuccessListener {
                    fetchCourses() // Refresh the list of courses
                    Toast.makeText(requireContext(), "Course added successfully!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to add course", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchCourses() {
        binding.progressbar.visibility = View.VISIBLE
        db.collection("courses")
            .get()
            .addOnSuccessListener { documents ->
                courseList.clear()
                for (document in documents) {
                    val courseId = document.get("courseId")?.toString() ?: ""
                    val courseName = document.getString("courseName") ?: ""
                    val courseDesc = document.getString("courseDesc") ?: ""
                    val courseAmount = document.getString("courseAmount") ?: ""
                    val courseOfferAmount = document.getString("offerAmount") ?: ""
                    val courseImgUrl = document.getString("courseImgUrl") ?: ""
                    val isCourseDisable = document.getBoolean("isCourseDisable") ?: false
                    val courseDate = document.getTimestamp("courseDate")?.toDate()
                    val subjects = document.get("subjects") as? List<Subject> ?: listOf()

                    val course = CourseModel(
                        courseId = courseId,
                        courseName = courseName,
                        courseDesc = courseDesc,
                        courseAmount = courseAmount,
                        offerAmount = courseOfferAmount,
                        courseImgUrl = courseImgUrl,
                        isCourseDisable = isCourseDisable,
                        courseDate = courseDate,
                        subjects = subjects
                    )
                    courseList.add(course)
                }
                if (courseList.isEmpty()) {
                    binding.progressbar.visibility = View.GONE
                    binding.helping.visibility=View.VISIBLE
                } else {
                    adapter.submitList(courseList)
                    adapter.notifyDataSetChanged()
                    binding.progressbar.visibility = View.GONE
                    binding.helping.visibility=View.GONE
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to fetch courses", Toast.LENGTH_SHORT).show()
                binding.progressbar.visibility = View.GONE
                binding.helping.visibility=View.VISIBLE
            }
    }

    private fun getCourseId(courseName: String): String {
        // Remove spaces and special characters from the course name
        val sanitizedCourseName = courseName.replace(Regex("[^a-zA-Z0-9]"), "")
        // Generate a random UUID and append it to the sanitized course name
        return "$sanitizedCourseName-${UUID.randomUUID()}"
    }

}