package com.parithidb.cgnews.ui.home

import android.graphics.Paint
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.textview.MaterialTextView
import com.parithidb.cgnews.R
import com.parithidb.cgnews.data.database.entities.ArticleEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ArticlesAdapter(
    private val onSourceClick: (String) -> Unit
) : PagingDataAdapter<ArticleEntity, ArticlesAdapter.NewsViewHolder>(COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_article, parent, false)
        return NewsViewHolder(view, onSourceClick)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    class NewsViewHolder(
        itemView: View,
        private val onSourceClick: (String) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val image = itemView.findViewById<ImageView>(R.id.tvImage)
        private val title = itemView.findViewById<MaterialTextView>(R.id.tvTitle)
        private val desc = itemView.findViewById<MaterialTextView>(R.id.tvDescription)
        private val source = itemView.findViewById<MaterialTextView>(R.id.tvSource)
        private val content = itemView.findViewById<MaterialTextView>(R.id.tvContent)
        private val date = itemView.findViewById<MaterialTextView>(R.id.tvDate)

        fun bind(article: ArticleEntity) {
            title.text = article.title
            desc.text = article.description ?: "No description"
            val sourceName = article.sourceName ?: "Unknown"
            val url = article.url

            val text = "Source: $sourceName"
            val spannable = SpannableString(text)

            val start = text.indexOf(sourceName)
            val end = start + sourceName.length

            spannable.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(itemView.context, android.R.color.holo_blue_dark)),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            if (url != null) {
                spannable.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        onSourceClick(url)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        ds.isUnderlineText = true
                    }
                }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            source.text = spannable
            source.movementMethod = LinkMovementMethod.getInstance()

            content.text = article.content ?: "No content available"
            val epoch = article.publishedAt ?: 0L
            val millis = if (epoch < 1_000_000_000_000L) epoch * 1000 else epoch // handle seconds
            date.text = millis.toFormattedDate()

            Glide.with(itemView.context)
                .load(article.imageUrl)
                .placeholder(R.drawable.ic_image_placeholder)
                .into(image)

            article.url.let { url ->
                source.setOnClickListener {
                    onSourceClick(url)
                }
            }
        }
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<ArticleEntity>() {
            override fun areItemsTheSame(old: ArticleEntity, new: ArticleEntity) =
                old.url == new.url

            override fun areContentsTheSame(old: ArticleEntity, new: ArticleEntity) =
                old == new
        }
    }

}

fun Long.toFormattedDate(): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(Date(this))
}

