package com.example.bookshelfapplication

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PostDao {
    private val database = Firebase.firestore
    private val auth = Firebase.auth
    val postCollections = database.collection("posts")
    private val currentUser = auth.currentUser

    fun addPost(title: String, price: Long, description: String, branch: String, year: String, contact: String, photoUrl: String) {
        val currentTime = System.currentTimeMillis()
        val post = Post(title,price,description,branch,year,contact, photoUrl, currentUser!!.displayName!!, currentUser!!.uid!! ,currentTime)
        postCollections.document(currentUser.uid+currentTime).set(post)
    }

    fun deletePost(documentPath : String){
        postCollections.document(documentPath).delete()
    }
}