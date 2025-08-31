package com.parithidb.cgnews.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.parithidb.cgnews.R
import com.parithidb.cgnews.data.database.entities.ArticleEntity
import com.parithidb.cgnews.databinding.ItemSearchBinding

class SearchAdapter(
    private val onItemClick: (ArticleEntity) -> Unit
) : PagingDataAdapter<ArticleEntity, SearchAdapter.SearchViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ArticleEntity>() {
            override fun areItemsTheSame(oldItem: ArticleEntity, newItem: ArticleEntity) =
                oldItem.url == newItem.url

            override fun areContentsTheSame(oldItem: ArticleEntity, newItem: ArticleEntity) =
                oldItem == newItem
        }
    }

    inner class SearchViewHolder(private val binding: ItemSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(article: ArticleEntity) {
            binding.tvTitle.text = article.title
            binding.tvDescription.text = article.description ?: "No description"
            binding.tvDate.text = article.publishedAt.toFormattedDate()

            Glide.with(binding.root.context)
                .load(article.imageUrl)
                .placeholder(R.drawable.ic_image_placeholder)
                .into(binding.ivNewsImage)

            binding.root.setOnClickListener {
                onItemClick(article)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = ItemSearchBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }
}

