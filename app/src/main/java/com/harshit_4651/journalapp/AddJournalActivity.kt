package com.harshit_4651.journalapp

import android.app.ComponentCaller
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.harshit_4651.journalapp.databinding.ActivityAddJournalBinding

class AddJournalActivity : AppCompatActivity() {


    lateinit var binding: ActivityAddJournalBinding

    // Credentials
    var currentUserId: String = ""
    var currentUserName: String = ""

    // Firebase
    lateinit var auth: FirebaseAuth
    lateinit var user: FirebaseUser

    // Firebase Storage
    var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    lateinit var storageReference: StorageReference
    var collectionReference: CollectionReference = db.collection("Journal")
    lateinit var imageUri: Uri


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // diff from course
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_journal)

        storageReference = FirebaseStorage.getInstance().getReference()

        auth = Firebase.auth

        binding.apply{
            postProgressBar.visibility = View.INVISIBLE

            if(JournalUser.instance!= null){
//                currentUserId = JournalUser.instance!!.userId.toString()
//                currentUserName = JournalUser.instance!!.userName.toString()

                currentUserId = auth.currentUser?.uid.toString()
                currentUserName = auth.currentUser?.displayName.toString()

                postUsernameTextview.text = currentUserName



            }

            // getting image from
            postCameraButton.setOnClickListener {
                var i: Intent = Intent(Intent.ACTION_GET_CONTENT)
                i.setType("image/*")
                startActivityForResult(i,1)
            }


            postSaveJournalButton.setOnClickListener {
                SaveJournal()
            }
        }



    }

    private fun SaveJournal() {

        var title : String = binding.postTitleEt.text.toString().trim()
        var thoughts : String = binding.postDescriptionEt.text.toString().trim()

        binding.postProgressBar.visibility = View.VISIBLE
        if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(thoughts) && imageUri != null){


            // Savings the path of images in storage
            // ...../journal_images/our_image.jpg

            val filePath: StorageReference = storageReference
                .child("journal_images")
                .child("my_image_"+ Timestamp.now().seconds)

            // uploading the images

            filePath.putFile(imageUri)
                .addOnSuccessListener {
                    filePath.downloadUrl.addOnSuccessListener {

                        var imageUri: String = it.toString()
                        var timestamp: Timestamp = Timestamp.now()


                        // Creating the object of Journal
                        var journal: Journal = Journal(
                            title,
                            thoughts,
                            imageUri,

                            currentUserId,
                            timestamp,
                            currentUserName

                        )

                        // adding the new journal
                        collectionReference.add(journal)
                            .addOnSuccessListener {
                                binding.postProgressBar.visibility = View.INVISIBLE
                                var i: Intent = Intent(this, JournalList::class.java)

                                startActivity(i)
                                finish()

                            }

                    }
                }.addOnFailureListener(){

                    binding.postProgressBar.visibility = View.INVISIBLE


                }


        }else{
            binding.postProgressBar.visibility = View.INVISIBLE

        }

    }


    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            imageUri = data?.data!! // getting the actual image path
            binding.postImageView.setImageURI(imageUri) // showing the image
        }

    }

    override fun onStart() {
        super.onStart()

        user = auth.currentUser!!

    }

    override fun onStop() {
        super.onStop()
        if (auth != null) {
        }
    }

}