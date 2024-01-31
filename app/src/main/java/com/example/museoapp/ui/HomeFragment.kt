package com.example.museoapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.museoapp.BaseFragment
import com.example.museoapp.MainActivity
import com.example.museoapp.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    private lateinit var firestore: FirebaseFirestore

    private lateinit var btnLogout: Button

    private lateinit var auth: FirebaseAuth
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnLogout = binding.btnLogout

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        //llamr al nombre del usuario y reemplazar en el textview id text_user
        firestore.collection("users").document(auth.currentUser?.uid.toString()).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val user = document.data
                    binding.textUser.text = user?.get("name").toString()
                }
            }

        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            activity?.finish()
            // IR HACIA MAIN ACTIVITY
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }

    }

}