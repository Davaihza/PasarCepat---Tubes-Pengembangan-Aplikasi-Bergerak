package com.example.pasarcepat.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Review(
    val id: String,
    val userName: String,
    val userAvatarUrl: String,
    val rating: Int,
    val date: String,
    val content: String
) : Parcelable
