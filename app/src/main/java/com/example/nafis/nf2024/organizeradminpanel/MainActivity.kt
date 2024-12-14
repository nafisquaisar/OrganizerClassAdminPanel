package com.example.nafis.nf2024.organizeradminpanel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.nafis.nf2024.organizeradminpanel.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object{
        lateinit var binding: ActivityMainBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

    }

    fun updateToolbar(title: String, showBackArrow: Boolean) {
        binding.titleName.text = title
        binding.backarrow.visibility = if (showBackArrow) View.VISIBLE else View.GONE
        if (showBackArrow) {
            binding.backarrow.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }


}
