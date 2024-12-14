package com.example.nafis.nf2024.organizeradminpanel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.nafis.nf2024.organizeradminpanel.databinding.FragmentDashBoardBinding


class DashBoardFragment : Fragment() {
   private lateinit var binding: FragmentDashBoardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentDashBoardBinding.inflate(inflater,container,false)
        binding.video.setOnClickListener {
            findNavController().navigate(R.id.action_dashBoardFragment_to_videoFragment)
        }
        binding.Note.setOnClickListener {
            findNavController().navigate(R.id.action_dashBoardFragment_to_noteFragment)
        }
        binding.PYQ.setOnClickListener {
            findNavController().navigate(R.id.action_dashBoardFragment_to_PYQFragment)
        }
        binding.test.setOnClickListener {
            findNavController().navigate(R.id.action_dashBoardFragment_to_testPanelFragment)
        }

        binding.Doubt.setOnClickListener {
            findNavController().navigate(R.id.action_dashBoardFragment_to_doubtFragment)
        }

        binding.Course.setOnClickListener {
            findNavController().navigate(R.id.action_dashBoardFragment_to_courseFragment)
        }
        return binding.root
    }



}