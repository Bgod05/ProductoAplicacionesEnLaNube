package com.example.museoapp.ui


import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.museoapp.ArFragment
import com.example.museoapp.BaseFragment
import com.example.museoapp.R
import com.example.museoapp.model.Culture
import com.example.museoapp.database.remote.Firestore
import com.example.museoapp.databinding.FragmentMuseumBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

class MuseumFragment : BaseFragment<FragmentMuseumBinding>(FragmentMuseumBinding::inflate)
{
    private val database = Firestore.getCulture()
    private lateinit var cultureRecycler: RecyclerView
    private lateinit var cultureArrayList: ArrayList<Culture>

    // firestore user

    private lateinit var firestore: FirebaseFirestore

    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cultureRecycler = binding.mainRecyclerView
        cultureArrayList = arrayListOf()
        cultureRecycler.layoutManager = LinearLayoutManager(context)
        cultureRecycler.setHasFixedSize(true)

        // firestore user

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        firestore.collection("users").document(auth.currentUser?.uid.toString()).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val user = document.getBoolean("isPremium")
                    if (user == true) {
                        binding.fab.visibility = View.VISIBLE
                        binding.fab.setOnClickListener {
                            val fragmentAr = ArFragment()
                            parentFragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, fragmentAr)
                                .addToBackStack(null)
                                .commit()
                        }
                    } else {
                        binding.fab.visibility = View.GONE
                    }
                }
            }

        getData()

    }

    private fun getData() {

        database.collection("cultures").addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null) {
                    return
                }
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        cultureArrayList.add(dc.document.toObject(Culture::class.java))
                    }
                }
                val adapter = Adapter(cultureArrayList)//this@MuseumFragment)
                cultureRecycler.adapter = adapter
            }


        })

    }

}

