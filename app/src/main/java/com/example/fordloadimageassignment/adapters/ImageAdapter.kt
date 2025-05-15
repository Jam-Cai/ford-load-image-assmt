package com.example.fordloadimageassignment.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fordloadimageassignment.databinding.ItemImageBinding
import com.example.fordloadimageassignment.models.ImageItem

// imageAdapter binds list of ImageItem objects to the RecyclerView
// it inflates the item layout and binds each ImageItem's data to the views
class ImageAdapter(
    private val imageList: List<ImageItem>,
    private val onItemClick: (position: Int) -> Unit
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    // called when RecyclerView needs a new ViewHolder to display an item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        // inflate the layout using view binding
        val binding = ItemImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ImageViewHolder(binding)
    }

    // called to display the data at the specified position
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imageList[position])
    }

    // returns the total number of items in the data set
    override fun getItemCount(): Int = imageList.size

    // ViewHolder class holds the item views
    inner class ImageViewHolder(private val binding: ItemImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            // set click listener for the whole item view
            binding.root.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    onItemClick(pos)
                }
            }
        }

        // bind the ImageItem data to the user interface elements
        fun bind(image: ImageItem) {
            // load image asynchronously into the ImageView using Glide
            Glide.with(binding.root.context)
                .load(image.uri)
                .centerCrop()
                .into(binding.imageView)

            binding.tvImageName.text = image.name
        }
    }
}
