package com.example.bookshelfapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_2.view.*

class MyRequestsAdapter(private val listener: (Request) -> Unit, private val listenerTwo: (Request) -> Unit): RecyclerView.Adapter<MyRequestsAdapter.RequestViewHolder>() {

    var dataset = mutableListOf<Request>()
        set(value){
            field = value
            notifyDataSetChanged()
        }

    class RequestViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val requestTitle: TextView = itemView.request_title
        val uploadTime: TextView = itemView.upload_time
        val createdBy: TextView = itemView.uploader_name
        val deleteButton: ImageButton = itemView.delete
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_2, parent, false)
        return RequestViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        holder.requestTitle.text = dataset[position].title
        holder.uploadTime.text = "Posted " + Utility.getTimeAgo(dataset[position].createdAt)
        holder.createdBy.text = "Post by " + dataset[position].createdBy

        holder.deleteButton.setOnClickListener {
            listenerTwo.invoke(dataset[position])
        }

        holder.itemView.setOnClickListener{
            listener.invoke(dataset[position])
        }
    }

    override fun getItemCount(): Int {
        return dataset.size
    }
}