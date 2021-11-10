package com.example.flickrapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.flickrapp.databinding.ItemRowBinding

class RecyclerViewAdapter(val activity: MainActivity, private val photos: ArrayList<Images>): RecyclerView.Adapter<RecyclerViewAdapter.ItemViewHolder>() {
    class ItemViewHolder(val binding: ItemRowBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemRowBinding.inflate
            (LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val photo = photos[position]

        holder.binding.apply {
            ImgTitleTV.text = photo.title
            Glide.with(activity).load(photo.link).into(Thumbnail)
            ItemRowLLO.setOnClickListener { activity.OPEN(photo.link) }
        }
    }

    override fun getItemCount() = photos.size
}