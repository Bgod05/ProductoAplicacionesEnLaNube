package com.example.museoapp.model

import com.google.firebase.firestore.Exclude
data class Culture(
    @Exclude val id: String? = null,
    val name: String? = null,
    val description: String? = null,
    val url: String? = null,
    val url3d: String? = null,
)