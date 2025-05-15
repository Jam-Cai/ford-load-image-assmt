package com.example.fordloadimageassignment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fordloadimageassignment.databinding.ActivityMainBinding
import com.example.fordloadimageassignment.fragments.ImageListFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(androidx.appcompat.R.style.Theme_AppCompat_Light_NoActionBar)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ImageListFragment())
                .commit()
        }
    }
}
