package com.example.citypulse

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PostAdapter : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private var postsList: List<PostData> = listOf()

    fun submitList(posts: List<PostData>) {
        postsList = posts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postsList[position]
        holder.bind(post)
    }

    override fun getItemCount(): Int {
        return postsList.size
    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtPostStatus: TextView = itemView.findViewById(R.id.txtPostStatus)
        private val imgPost: ImageView = itemView.findViewById(R.id.imgPost)

        fun bind(post: PostData) {
            txtPostStatus.text = post.status
            Glide.with(itemView.context)
                .load(post.imageUrl)
                .into(imgPost)
        }
    }
}

