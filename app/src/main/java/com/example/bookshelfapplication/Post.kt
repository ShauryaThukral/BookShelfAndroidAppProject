package com.example.bookshelfapplication

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Post (val title: String,
            val price: Long,
            val description: String,
            val branch: String,
            val year: String,
            val contact: String,
            val photoUrl: String,
            val createdBy: String,
            val uid: String,
            val createdAt: Long): Parcelable