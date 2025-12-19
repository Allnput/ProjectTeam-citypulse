package com.example.citypulse

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PostsAdapter : RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {

    private var postsList: MutableList<PostData> = mutableListOf()

    fun submitList(posts: List<PostData>) {
        postsList.clear()
        postsList.addAll(posts)
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

    override fun getItemCount(): Int = postsList.size

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Pastikan ID ini sesuai dengan item_post.xml Anda
        private val txtPostStatus: TextView = itemView.findViewById(R.id.txtPostStatus)
        private val imgPost: ImageView = itemView.findViewById(R.id.imgPost)

        fun bind(post: PostData) {
            // Menggunakan properti 'status' sesuai PostData terbaru Anda
            txtPostStatus.text = post.status

            // Menampilkan gambar jika ada URL-nya
            if (!post.imageUrl.isNullOrEmpty()) {
                imgPost.visibility = View.VISIBLE
                Glide.with(itemView.context)
                    .load(post.imageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.stat_notify_error)
                    .into(imgPost)
            } else {
                // Sembunyikan ImageView jika tidak ada gambar agar layout tetap rapi
                imgPost.visibility = View.GONE
            }
        }
    }
}