package com.harshit_4651.journalapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.harshit_4651.journalapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    // Firebase Auth
    private lateinit var auth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.createAcctBTN.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.emailSignInButton.setOnClickListener() {

            LoginWithEmailPassword(
                binding.email.text.toString().trim(),
                binding.password.text.toString().trim()
            )


        }

        // Auth Ref
        auth = Firebase.auth



    }

    private fun LoginWithEmailPassword(email: String, password: String){

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {
                    // Sign in Success

                    var journal: JournalUser = JournalUser.instance!!
                    journal.userId = auth.currentUser?.uid
                    journal.userName = auth.currentUser?.displayName


                    goToJournalList()
                }else{
                    // Sign in failed
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }

            }

    }


    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser

        if(currentUser != null){
            goToJournalList()
        }
    }

    private fun goToJournalList() {
        var intent = Intent(this, JournalList::class.java)
        startActivity(intent)
    }
}