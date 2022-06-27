package com.example.bookshelfapplication

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.list_item.view.*

class PostListAdapter(private val listener: (Post) -> Unit): RecyclerView.Adapter<PostListAdapter.PostViewHolder>()  {

    var dataset = mutableListOf<Post>()
    set(value){
        field = value
        notifyDataSetChanged()
    }

    class PostViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val bookPhoto: ImageView = itemView.book_photo
        val postTitle: TextView = itemView.post_title
        val price: TextView = itemView.price
        val uploadTime: TextView = itemView.upload_time
        val targetAudience: TextView = itemView.target_audience
        val uploaderName: TextView = itemView.uploader_name
        val deleteButton: ImageButton = itemView.delete
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return PostViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.postTitle.text = dataset[position].title.toString()
        holder.price.text = "Rs. " + dataset[position].price.toString()
        holder.uploadTime.text = "Posted " + Utility.getTimeAgo(dataset[position].createdAt)
        holder.targetAudience.text = dataset[position].branch + ", " + dataset[position].year
        holder.uploaderName.text = "Post by " + dataset[position].createdBy
        holder.deleteButton.visibility = View.INVISIBLE

        val url = Uri.parse(dataset[position].photoUrl)
        Glide.with(holder.postTitle.context).load(url).into(holder.bookPhoto)

        holder.itemView.setOnClickListener{
            listener.invoke(dataset[position])
        }
    }

    override fun getItemCount(): Int {
        return dataset.size
    }
}