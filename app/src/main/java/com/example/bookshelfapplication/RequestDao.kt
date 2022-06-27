package com.example.bookshelfapplication

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RequestDao {
    private val database = Firebase.firestore
    private val auth = Firebase.auth
    val requestCollections = database.collection("requests")
    private val currentUser = auth.currentUser

    fun addRequest(title: String, contact: String, description: String){
        val currentTime = System.currentTimeMillis()
        val request = Request(title,description,contact,currentUser!!.displayName!!,currentUser!!.uid!! ,currentTime)
        requestCollections.document(currentUser.uid+currentTime).set(request)
    }

    fun deleteRequest(documentPath: String){
        requestCollections.document(documentPath).delete()
    }
}