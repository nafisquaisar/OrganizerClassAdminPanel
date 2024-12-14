package com.example.nafis.nf2024.organizeradminpanel

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nafis.nf2024.organizeradminpanel.Adapter.VideoAdapter
import com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack.VideoCallback
import com.example.nafis.nf2024.organizeradminpanel.Model.Video
import com.example.nafis.nf2024.organizeradminpanel.databinding.FragmentVideoViewBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID


//==========course Video Fragment View=================
class VideoFragmentView(var courseId: String?,var courseName:String?,var subjectId: String?,var chapterId: String?) : Fragment() {
  private lateinit var binding:FragmentVideoViewBinding
    private lateinit var videoList :ArrayList<Video>
    private lateinit var videoAdapter: VideoAdapter
    private val db=Firebase.firestore

    private val callback by lazy {
        object :VideoCallback{
            override fun onVideoClick(item: Video) {

            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun updateVideoClick(item: Video) {
                 addNoteDialog(item,true)
            }

            override fun deleteVideoClick(itemId: String) {
                deleteNote(itemId)
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentVideoViewBinding.inflate(inflater,container,false)

        videoList= ArrayList()
        videoAdapter= VideoAdapter(requireContext(),callback,videoList)
        binding.VideoRecyclerView.layoutManager=LinearLayoutManager(requireContext())
        binding.VideoRecyclerView.adapter=videoAdapter

       fetchVideo()
        binding.addVideo.setOnClickListener {
            addNoteDialog(isUpdate=false)
        }


        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun addNoteDialog(item: Video? = null, isUpdate: Boolean) {
        val dialog= Dialog(requireContext())
        dialog.setContentView(R.layout.add_video_dialog)
        dialog.show()

        val videoNameEdit=dialog.findViewById<EditText>(R.id.dialogVideoTitle)
        val videoDesEdit=dialog.findViewById<EditText>(R.id.dialogVideoDes)
        val videoUrlEdit=dialog.findViewById<EditText>(R.id.dialogVideoUrl)

        val saveNoteBtn=dialog.findViewById<Button>(R.id.dialogUplaodVideoBtn)
        val cancelBtn=dialog.findViewById<Button>(R.id.dialogCancelVideoBtn)

        if(isUpdate && item!=null){
            saveNoteBtn.setText("Update")
            videoNameEdit.setText(item.videoTitle)
            videoDesEdit.setText(item.des)
            videoUrlEdit.setText(item.videoUrl)

            saveNoteBtn.setOnClickListener {
                val videoTitle=videoNameEdit.text.toString().trim()
                val videoDes=videoDesEdit.text.toString().trim()
                val videoUrl=videoUrlEdit.text.toString().trim()
                uploadCourseVideo(videoTitle,videoDes,videoUrl,isUpdate,item)
                dialog.dismiss()
            }

        }else{
            saveNoteBtn.setOnClickListener {
                val videoTitle=videoNameEdit.text.toString().trim()
                val videoDes=videoDesEdit.text.toString().trim()
                val videoUrl=videoUrlEdit.text.toString().trim()
                uploadCourseVideo(videoTitle, videoDes, videoUrl, isUpdate= isUpdate)
                dialog.dismiss()
            }
        }



        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun uploadCourseVideo(
        videoTitle: String,
        videoDes: String,
        videoUrl: String,
        isUpdate: Boolean,
        item:Video?=null
    ) {



        if(isUpdate && item!=null){
            val video= hashMapOf(
                "videoId" to item.videoId,
                "videoTitle" to videoTitle,
                "videoUrl" to videoUrl,
                "des" to videoDes,
                "date" to item.date,
                "time" to item.time
            )

            try {
                db.collection("courses")
                    .document(courseId!!)
                    .collection("subjects")
                    .document(subjectId!!)
                    .collection("Chapters")
                    .document(chapterId!!)
                    .collection("Videos")
                    .document(item.videoId)
                    .update(video as Map<String,Any>)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Data Update successfully", Toast.LENGTH_SHORT).show()
                        // Dismiss the progress dialog on the UI thread after successful Firestore upload
                        activity?.runOnUiThread {
                            ProgressDialogUtil.dismissProgressDialog()
                        }
                        fetchVideo()
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
        }else{
            // Generate unique ID and get the current date
            val id = generateLongFromUUID(videoTitle)
            val date = getCurrentDate()
            val time=getCurrentTime()

            val firestore = Firebase.firestore

            val video= hashMapOf(
                "videoId" to id,
                "videoTitle" to videoTitle,
                "videoUrl" to videoUrl,
                "des" to videoDes,
                "date" to date,
                "time" to time
            )

            try {
                firestore.collection("courses")
                    .document(courseId!!)
                    .collection("subjects")
                    .document(subjectId!!)
                    .collection("Chapters")
                    .document(chapterId!!)
                    .collection("Videos")
                    .document(id)
                    .set(video)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Data uploaded successfully", Toast.LENGTH_SHORT).show()
                        // Dismiss the progress dialog on the UI thread after successful Firestore upload
                        activity?.runOnUiThread {
                            ProgressDialogUtil.dismissProgressDialog()
                        }
                        fetchVideo()
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

    private fun fetchVideo() {
       binding.progressbar.visibility=View.VISIBLE
        val db=Firebase.firestore
        videoList.clear()
        db.collection("courses")
            .document(courseId!!)
            .collection("subjects")
            .document(subjectId!!)
            .collection("Chapters")
            .document(chapterId!!)
            .collection("Videos")
            .get()
            .addOnSuccessListener {Videos->
                for (vid in Videos){
                    val video=vid.toObject(Video::class.java)
                    videoList.add(video)
                }

                if(videoList.isEmpty()){
                    binding.progressbar.visibility = View.GONE
                    binding.helping.visibility=View.VISIBLE
                } else {
                    videoAdapter.submitList(videoList)
                    videoAdapter.notifyDataSetChanged()
                    binding.progressbar.visibility = View.GONE
                    binding.helping.visibility=View.GONE
                }

            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to fetch Video: ${it.message}", Toast.LENGTH_SHORT).show()
                binding.progressbar.visibility = View.GONE
                binding.helping.visibility=View.VISIBLE
            }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDate(): String {
        val currentDate = LocalDate.now()
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return currentDate.format(dateFormatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentTime(): String {
        val currentTime = LocalTime.now()
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss") // Format: 24-hour time (HH:mm:ss)
        return currentTime.format(timeFormatter)
    }

    //==============Delete the Funtion==============
    private fun deleteNote(itemId: String) {
        val chapterRef =   db.collection("courses")
            .document(courseId!!)
            .collection("subjects")
            .document(subjectId!!)
            .collection("Chapters")
            .document(chapterId!!)
            .collection("Videos")
            .document(itemId)

        AlertDialog.Builder(requireContext())
            .setTitle("Delete Video")
            .setMessage("Are you sure you want to delete this video?")
            .setPositiveButton("Yes") { _, _ ->
                chapterRef.delete()
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "video deleted successfully", Toast.LENGTH_SHORT).show()
                        fetchVideo() // Refresh the list
                        videoAdapter.notifyDataSetChanged() // .notifyDataSetChanged()
                    }
                    .addOnFailureListener { error ->
                        Toast.makeText(requireContext(), "Failed to delete: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("No", null)
            .show()
    }


    fun generateLongFromUUID(video:String): String {
        val sanatizedVideoName=video.replace(Regex("[^a-zA-Z0-9]"),"")
        return "${sanatizedVideoName}-${UUID.randomUUID()}"
    }
}