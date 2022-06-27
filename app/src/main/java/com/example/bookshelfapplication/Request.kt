package com.example.bookshelfapplication

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Request (val title: String,
                    val description: String,
                    val contact: String,
                    val createdBy: String,
                    val uid: String,
                    val createdAt: Long): Parcelable