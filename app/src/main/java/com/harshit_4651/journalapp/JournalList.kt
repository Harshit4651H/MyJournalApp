package com.harshit_4651.journalapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
// Removed: import com.google.firebase.firestore.core.View
import com.google.firebase.storage.StorageReference

import com.harshit_4651.journalapp.databinding.ActivityJournalListBinding



class JournalList : AppCompatActivity() {

    lateinit var binding: ActivityJournalListBinding

    // Firebase References
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var User: FirebaseUser
    var db = FirebaseFirestore.getInstance()

    lateinit var storageReference: StorageReference

    lateinit var journalList: MutableList<Journal>
    lateinit var adapter: JournalRecyclerAdapter
    var collectionReference: CollectionReference = db.collection("Journal")

    lateinit var noPostsText: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_journal_list)
        // Use binding to access the toolbar
        setSupportActionBar(binding.toolbar)

        // Firebase Auth
        firebaseAuth = Firebase.auth
        User = firebaseAuth.currentUser!!

        // Recycler View
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this)

        // Post arrayList
        journalList = arrayListOf<Journal>()

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add -> {
                if (User != null && firebaseAuth != null) {
                    val intent = Intent(this, AddJournalActivity::class.java)
                    startActivity(intent)
                }
                return true // Return true when item is handled
            }

            R.id.action_signout -> {
                if (User != null && firebaseAuth != null) {
                    firebaseAuth.signOut()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Optional: finish JournalList activity after sign out
                }
                return true // Return true when item is handled
            }


        }
        return super.onOptionsItemSelected(item)
    }


    // Getting all posts
    override fun onStart() {
        super.onStart()

        collectionReference.whereEqualTo("userId",
            User.uid)
            .get()
            .addOnSuccessListener {
                Log.i("TAGY", "Sizey: ${it.size()}")
                if (!it.isEmpty) {
                    journalList.clear() // Clear list before adding new data
                    Log.i("TAGY", "Elements: ${it}")

                    for (document in it) {

                        val journalTimestamp = document.data.get("timeStamp") as? Timestamp ?: Timestamp.now()

                        var journal = Journal(
                            document.data["title"]?.toString() ?: "",
                            document.data.get("thoughts")?.toString() ?: "",
                            document.data.get("imageUrl")?.toString() ?: "",
                            document.data.get("userId")?.toString() ?: "",
                            journalTimestamp,
                            document.data.get("userName")?.toString() ?: ""
                        )


                        journalList.add(journal)

                    }



                    // RecyclerView
                    adapter = JournalRecyclerAdapter(
                        this, journalList
                    )
                    binding.recyclerView.setAdapter(adapter)
                    adapter.notifyDataSetChanged()
                    binding.listNoPosts.visibility = View.INVISIBLE // Hide if posts exist

                } else {
                    binding.listNoPosts.visibility = View.VISIBLE
                    journalList.clear() // Ensure list is empty
                    if(::adapter.isInitialized) { // Check if adapter is initialized
                        adapter.notifyDataSetChanged() // Notify adapter of empty list
                    }
                }

            }.addOnFailureListener {
                Log.e("JournalList", "Error fetching journals: ${it.message}", it)
                Toast.makeText(this,
                    "Opps! Something went wrong while fetching posts!: ${it.message}",
                    Toast.LENGTH_LONG).show()
            }


    }
}