package com.example.nafis.nf2024.organizeradminpanel.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nafis.nf2024.organizeradminpanel.Adapter.DoubtListAdapter
import com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack.DoubtListCallBack
import com.example.nafis.nf2024.organizeradminpanel.MainActivity
import com.example.nafis.nf2024.organizeradminpanel.Model.DoubtModel
import com.example.nafis.nf2024.organizeradminpanel.R
import com.example.nafis.nf2024.organizeradminpanel.databinding.FragmentDoubtListBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DoubtListFragment : Fragment() {
    private lateinit var binding: FragmentDoubtListBinding
    private lateinit var doubtList: ArrayList<DoubtModel>
    private lateinit var adapter: DoubtListAdapter

    private val callBack by lazy {
        object : DoubtListCallBack {
            override fun DoubtListClick(item: DoubtModel, position: Int) {
                // Handle click actions
                val bundle = Bundle().apply {
                    putString("uid", item.userUid)
                    putString("doubtId", item.doubtId)
                    putString("userName", item.userName)
                    putString("userEmail", item.userEmail)
                    putString("userImgUrl", item.userImgUrl)
                    putString("studQuesTitle", item.studQuesTitle)
                    putString("studQuesDesc", item.studQuesDesc)
                    putString("studQuesImgUrl", item.studQuesImgUrl)
                    putString("teachAnsDesc", item.teachAnsDesc)
                    putString("teachAnsImgUrl", item.teachAnsImgUrl)
                    putInt("position", position)
                    putLong("timestamp", item.timestamp)
                }
                // Update the status before navigating
                updateStatus(item.doubtId, position,item.userUid,item.status)
                Toast.makeText(requireContext(), "${item.status}", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_doubtListFragment_to_doubtSolveFragment, bundle)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentDoubtListBinding.inflate(inflater, container, false)

        // Get UID from arguments
        val uid = arguments?.getString("uid") ?: return binding.root

        // Set up toolbar title explicitly in DoubtListFragment
        MainActivity.binding.backarrow.visibility = View.VISIBLE
        MainActivity.binding.titleName.text = "Doubt"
        MainActivity.binding.backarrow.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Initialize RecyclerView
        doubtList = ArrayList()
        adapter = DoubtListAdapter(binding.root.context, callBack)
        binding.DoubtListRecylerView.adapter = adapter
        binding.DoubtListRecylerView.layoutManager = LinearLayoutManager(binding.root.context)

        // Fetch doubts from Firebase
        fetchDoubts(uid)

        return binding.root
    }

    private fun fetchDoubts(uid: String) {
        binding.progressbar.visibility = View.VISIBLE
        val ref = FirebaseDatabase.getInstance().getReference("Doubt").child(uid)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                doubtList.clear()
                for (snap in snapshot.children) {
                    val doubt = snap.getValue(DoubtModel::class.java)
                    if (doubt != null) {
                        doubtList.add(doubt)
                    }
                }
                doubtList.sortByDescending { it.timestamp }
                if (isAdded) {
                    if (doubtList.isEmpty()) {
                        binding.placeholderText.visibility = View.VISIBLE
                        binding.DoubtListRecylerView.visibility = View.GONE
                        binding.progressbar.visibility = View.GONE
                    } else {
                        binding.placeholderText.visibility = View.GONE
                        binding.progressbar.visibility = View.GONE
                        binding.DoubtListRecylerView.visibility = View.VISIBLE
                        adapter.submitList(ArrayList(doubtList))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DoubtListFragment", "Error fetching doubts: ${error.message}")
                Toast.makeText(context, "Failed to load doubts. Try again later.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateStatus(doubtId: String?, position: Int, userUid: String?, currentStatus: Boolean?) {
        if (doubtId == null) {
            Log.e("DoubtListFragment", "Doubt ID is null, cannot update status")
            return
        }

        val reference = FirebaseDatabase.getInstance().getReference("Doubt").child(userUid!!).child(doubtId)

        // Decide the status based on the current state
        val newStatus = when (currentStatus) {
            null -> true
            true -> true
            else -> false
        }

        // Update the status in Firebase
        val updatedDoubt = mapOf<String, Any>("status" to newStatus)

        reference.updateChildren(updatedDoubt).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("DoubtModel", "Status updated successfully")
                // Notify the adapter that the item has changed
                adapter.notifyItemChanged(position)
            } else {
                Log.e("DoubtModel", "Failed to update status: ${task.exception?.message}")
            }
        }.addOnFailureListener { exception ->
            Log.e("DoubtModel", "Error updating status: ${exception.message}")
        }
    }
}
