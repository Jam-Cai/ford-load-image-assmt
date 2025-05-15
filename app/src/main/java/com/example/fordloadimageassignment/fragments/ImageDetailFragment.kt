package com.example.fordloadimageassignment.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.fordloadimageassignment.databinding.FragmentImageDetailBinding
import com.example.fordloadimageassignment.models.ImageItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ImageDetailFragment : Fragment() {

    private var _binding: FragmentImageDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var imageItem: ImageItem

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getParcelable(ARG_IMAGE_ITEM, ImageItem::class.java)?.let {
            imageItem = it
            displayImageDetails()
        }

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun displayImageDetails() {
        Glide.with(this)
            .load(imageItem.uri)
            .into(binding.imageView)

        binding.tvImageName.text = "Name: ${imageItem.name}"
        binding.tvImageSize.text = "Size: ${formatFileSize(imageItem.size)}"
        binding.tvImageDimensions.text = "Dimensions: ${imageItem.width} × ${imageItem.height}"

        val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        val dateString = dateFormat.format(Date(imageItem.dateAdded * 1000))
        binding.tvImageDate.text = "Date: $dateString"

        binding.tvImageUri.text = "URI: ${imageItem.uri}"
    }

    private fun formatFileSize(size: Long): String {
        val kb = size / 1024.0
        return when {
            kb < 1024 -> String.format("%.2f KB", kb)
            else -> String.format("%.2f MB", kb / 1024.0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_IMAGE_ITEM = "arg_image_item"

        fun newInstance(imageItem: ImageItem): ImageDetailFragment {
            return ImageDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_IMAGE_ITEM, imageItem)
                }
            }
        }
    }
}
