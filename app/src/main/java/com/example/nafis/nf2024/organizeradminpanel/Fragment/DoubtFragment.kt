package com.example.nafis.nf2024.organizeradminpanel.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nafis.nf2024.organizeradminpanel.Adapter.DoubtUserAdapter
import com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack.DoubtUserCallBack
import com.example.nafis.nf2024.organizeradminpanel.MainActivity
import com.example.nafis.nf2024.organizeradminpanel.Model.UserProfile
import com.example.nafis.nf2024.organizeradminpanel.R
import com.example.nafis.nf2024.organizeradminpanel.databinding.FragmentDoubtBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DoubtFragment : Fragment() {
    private lateinit var binding: FragmentDoubtBinding
    private var userList = ArrayList<UserProfile>() // Initialize ArrayList
    private lateinit var adapter: DoubtUserAdapter

    private val callBack by lazy {
        object : DoubtUserCallBack {
            override fun DoubtUserClick(item: UserProfile) {
                val bundle = Bundle().apply {
                    putString("uid", item.uid)
                    putString("userName", item.UserName)
                }
                findNavController().navigate(R.id.action_doubtFragment_to_doubtListFragment, bundle)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDoubtBinding.inflate(inflater, container, false)

        // Set up toolbar
        MainActivity.binding.backarrow.visibility = View.VISIBLE
        MainActivity.binding.titleName.text = "Doubt User"
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


        // Set up RecyclerView
        binding.DoubtUserRecylerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = DoubtUserAdapter(requireContext(), callBack)
        binding.DoubtUserRecylerView.adapter = adapter

        // Fetch users
        fetchUsers()

        return binding.root
    }

    private fun fetchUsers() {
        binding.progressbar.visibility = View.VISIBLE
        val ref = FirebaseDatabase.getInstance().getReference("Doubt")
        userList.clear() // Clear old data

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (snap in snapshot.children) { // Iterate over UIDs
                    val uid = snap.key // Get UID

                    var hasUnresolvedDoubt = false
                    var hasNewDoubt = false
                    var userName: String? = null
                    var userEmail: String? = null
                    var userImgUrl: String? = null

                    // Iterate over each user's doubts
                    for (doubtSnap in snap.children) { // Iterate over user's doubts
                        userName = doubtSnap.child("userName").value as? String
                        userEmail = doubtSnap.child("userEmail").value as? String
                        userImgUrl = doubtSnap.child("userImgUrl").value as? String
                        val solved = doubtSnap.child("solved").value as? Boolean
                        val status = doubtSnap.child("status").value as? Boolean

                        // Check each doubt's status
                        if (solved == false && status!=null) {
                            hasUnresolvedDoubt = true
                        }
                        if (status == null) {
                            hasNewDoubt = true
                        }
                    }

                    // Determine overall status for the user after checking all doubts
                    if (userName != null && userEmail != null) {
                        val status = when {
                            hasUnresolvedDoubt -> "yellow" // User has unresolved doubts
                            hasNewDoubt -> "green"         // User has new doubts
                            else -> "hidden"               // All doubts are resolved
                        }

                        // Add user profile to the list
                        userList.add(UserProfile(uid, userName, userEmail, userImgUrl, status))
                    }
                }

                // Update UI
                if (isAdded) {
                    if (userList.isEmpty()) {
                        binding.placeholderText.visibility = View.VISIBLE
                    } else {
                        binding.placeholderText.visibility = View.GONE
                    }
                    adapter.submitList(userList)
                    binding.progressbar.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DoubtFragment", "Error fetching users: ${error.message}")
            }
        })
    }




 }
