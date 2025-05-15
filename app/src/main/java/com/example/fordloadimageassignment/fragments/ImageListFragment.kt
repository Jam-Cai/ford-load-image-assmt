package com.example.fordloadimageassignment.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.fordloadimageassignment.R
import com.example.fordloadimageassignment.adapters.ImageAdapter
import com.example.fordloadimageassignment.databinding.FragmentImageListBinding
import com.example.fordloadimageassignment.models.ImageItem
import com.example.fordloadimageassignment.utils.ImageLoader
import com.example.fordloadimageassignment.utils.RemoteImageDownloader
import kotlinx.coroutines.launch

class ImageListFragment : Fragment() {

    private var _binding: FragmentImageListBinding? = null
    private val binding get() = _binding!!

    private lateinit var imageAdapter: ImageAdapter
    private lateinit var imageLoader: ImageLoader
    private lateinit var remoteImageDownloader: RemoteImageDownloader

    private val imageList = mutableListOf<ImageItem>()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            loadImagesFromDevice()
        } else {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageLoader = ImageLoader(requireContext())
        remoteImageDownloader = RemoteImageDownloader(requireContext())

        setupRecyclerView()
        setupButtons()
        checkPermissionAndLoadImages()
    }

    private fun setupRecyclerView() {
        imageAdapter = ImageAdapter(imageList) { position ->
            // Navigate to detail fragment using fixed container id
            val detailFragment = ImageDetailFragment.newInstance(imageList[position])
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = imageAdapter
        }
    }

    private fun setupButtons() {
        binding.btnDownloadRemote.setOnClickListener {
            downloadRemoteImages()
        }

        binding.progressBar.visibility = View.GONE
    }

    private fun checkPermissionAndLoadImages() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES
        else
            Manifest.permission.READ_EXTERNAL_STORAGE

        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                loadImagesFromDevice()
            }
            shouldShowRequestPermissionRationale(permission) -> {
                Toast.makeText(
                    requireContext(),
                    "Storage permission is needed to show your images",
                    Toast.LENGTH_LONG
                ).show()
                requestPermissionLauncher.launch(permission)
            }
            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private fun loadImagesFromDevice() {
        binding.progressBar.visibility = View.VISIBLE

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val images = imageLoader.loadImagesFromDevice()

                imageList.clear()
                imageList.addAll(images)
                imageAdapter.notifyDataSetChanged()

                binding.emptyView.visibility = if (imageList.isEmpty()) View.VISIBLE else View.GONE
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error loading images", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun downloadRemoteImages() {
        binding.progressBar.visibility = View.VISIBLE

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val remoteImages = remoteImageDownloader.downloadImage(400,400, listOf(":)", ":D", ":P", ":o", ":|", ":(", ":'(", "xD", "XD", "o_o", "O_O", "T_T").random())

                imageList.add(0, remoteImages)
                imageAdapter.notifyItemRangeInserted(0, 1)
                binding.recyclerView.scrollToPosition(0)

                binding.emptyView.visibility = View.GONE

                Toast.makeText(requireContext(), "Remote image downloaded", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error downloading images", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
