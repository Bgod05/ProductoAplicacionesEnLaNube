package com.example.museoapp.model

data class User(
     val id: String? = null,
    val user: String? = null,
    val name: String? = null,
    val lastname: String? = null,
    val email: String? = null,
    val isPremium: Boolean? = null,
) {
    fun toMap(): MutableMap<String, Any> {
        return mutableMapOf(
            "id" to this.id!!,
            "user" to this.user!!,
            "name" to this.name!!,
            "lastname" to this.lastname!!,
            "email" to this.email!!,
            "isPremium" to this.isPremium!!,
        )
    }
}