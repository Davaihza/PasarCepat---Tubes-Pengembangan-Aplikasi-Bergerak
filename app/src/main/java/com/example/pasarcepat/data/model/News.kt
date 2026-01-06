package com.example.pasarcepat.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class News(
    val title: String,
    val date: String,
    val description: String,
    val imageUrl: String
) : Parcelable
