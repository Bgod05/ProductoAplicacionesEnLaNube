package com.example.museoapp.database.remote

import com.google.firebase.firestore.FirebaseFirestore
class Firestore {
    companion object {
        fun getCulture(): FirebaseFirestore {
            return FirebaseFirestore.getInstance()
        }
    }
}