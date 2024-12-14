package com.example.nafis.nf2024.organizeradminpanel

import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.google.firebase.storage.UploadTask

object ProgressDialogUtil {

    private var progressDialog: Dialog? = null


    fun showProgressDialog(context: Context, uploadTask: UploadTask) {
        // Create a dialog instance
        progressDialog = Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.progress_dailog)
            setCancelable(false) // Prevent dismissal while uploading
        }

        // Get the window of the dialog and set its layout parameters
        progressDialog?.window?.apply {
            val params = attributes
            params?.width = WindowManager.LayoutParams.MATCH_PARENT // Full width
            params?.height = WindowManager.LayoutParams.WRAP_CONTENT // Wrap content
            params?.horizontalMargin = 0.1f // Set horizontal margin as percentage
            attributes = params
        }

        // Find views
        val progressBar = progressDialog?.findViewById<ProgressBar>(R.id.progressShow)
        val progressPercentage = progressDialog?.findViewById<TextView>(R.id.progressPercentage)
        val status = progressDialog?.findViewById<TextView>(R.id.uploadingLabel)

        // Set initial progress
        progressBar?.progress = 0
        progressPercentage?.text = "0%"

        // Show dialog
        progressDialog?.show()

        // Update progress dynamically based on upload bytes
        uploadTask.addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()

            // Update progress bar and percentage text
            progressBar?.progress = progress
            progressPercentage?.text = "$progress%"
        }.addOnSuccessListener {
            // Handle successful upload
            progressPercentage?.text = "100%"
            status?.text = "Completed"

            // Dismiss dialog after a short delay
            Handler(Looper.getMainLooper()).postDelayed({
                dismissProgressDialog()
            }, 1000) // 1-second delay for user visibility
        }.addOnFailureListener {
            // Handle failed upload
            progressPercentage?.text = "Upload Failed"
            status?.text = "Failed"

            // Dismiss dialog after a short delay
            Handler(Looper.getMainLooper()).postDelayed({
                dismissProgressDialog()
            }, 1000)
        }
    }


    fun showProgress(context: Context, progress: Int) {
        val progressDialog = Dialog(context)
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialog.setContentView(R.layout.progress_dailog)
        progressDialog.setCancelable(false)

        // Get the window of the dialog and set its layout parameters
        val window = progressDialog.window
        val params = window?.attributes
        params?.width = WindowManager.LayoutParams.MATCH_PARENT // Full width
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT // Wrap content
        params?.horizontalMargin = 0.1f // Set horizontal margin as percentage (e.g., 10% margin)
        window?.attributes = params

        // Find views
        val progressBar = progressDialog.findViewById<ProgressBar>(R.id.progressShow)
        val progressPercentage = progressDialog.findViewById<TextView>(R.id.progressPercentage)

        // Set initial progress
        progressBar.progress = progress
        progressPercentage.text = "$progress%"

        // Show dialog
        progressDialog.show()

        // Simulate dynamic progress update without blocking the UI thread
        var currentProgress = progress
        val updateRunnable = object : Runnable {
            override fun run() {
                // Update progress
                if (currentProgress <= 100) {
                    progressBar.progress = currentProgress
                    progressPercentage.text = "$currentProgress%"
                    currentProgress += 20 // Increment by 10% for demo

                    // Post the next update after a delay (non-blocking)
                    progressBar.postDelayed(this, 500) // Update every 500ms
                } else {
                    progressDialog.dismiss() // Dismiss dialog after reaching 100%
                }
            }
        }

        // Start updating progress
        progressBar.post(updateRunnable)
    }



    fun dismissProgressDialog() {
        if (progressDialog != null && progressDialog?.isShowing == true) {
            Handler(Looper.getMainLooper()).post {
                progressDialog?.dismiss()
                progressDialog = null
            }
        }
    }


    // Update progress in the dialog
    fun updateProgress(progress: Int) {
        progressDialog?.findViewById<ProgressBar>(R.id.progressShow)?.progress = progress
        progressDialog?.findViewById<TextView>(R.id.progressPercentage)?.text = "$progress%"
    }


}

