package com.example.blogapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import coil.load
import com.example.blogapp.Model.BlogItemModel
import com.example.blogapp.databinding.ActivityReadMoreBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ReadMoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReadMoreBinding
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadMoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.saveBtn.setOnClickListener {
            startActivity(Intent(this, SavedArticlesActivity::class.java))
            finish()
        }
        auth = FirebaseAuth.getInstance()
        val blogs = intent.getParcelableExtra<BlogItemModel>("blogItem")
        if (blogs != null) {
            // Retrieve user related data here e. x blog title etc.
            binding.titleText.text = blogs.heading
            binding.userName.text = blogs.userName
            binding.date.text = blogs.date
            binding.blogDescriptionTextView.text = blogs.post

            val userImageUrl = blogs.profileImage
            binding.profileImage.load(userImageUrl) {
                placeholder(R.drawable.dog)
            }

            // Obtain the blogId
            val blogId = blogs.postId
            val userId = auth.currentUser?.uid

            // Check if the blog is saved
            val userReference =
                databaseReference.child("users").child(userId!!).child("saveBlogPosts")
                    .child(blogId)

            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val isSaved = dataSnapshot.getValue(Boolean::class.java) ?: false
                    // Update UI based on the value of isSaved
                    if (isSaved) {
                        // if blog already saved
                        binding.saveBtn.setImageResource(R.drawable.save_articles_fill_red)
                    } else {
                        // if blog not saved yet
                        binding.saveBtn.setImageResource(R.drawable.unsave_articles_red)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle any errors
                    Log.e("SAVED_DATA", "Error fetching saved status", databaseError.toException())
                }
            })

            // Check if the blog is saved
            val likeUserReference =
                databaseReference.child("users").child(userId).child("likes")
                    .child(blogId)

            likeUserReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val isLiked = dataSnapshot.getValue(Boolean::class.java) ?: false
                    // Update UI based on the value of isSaved
                    if (isLiked) {
                        // if blog already saved
                        binding.likeBtn.setImageResource(R.drawable.heart_fill_red)
                    } else {
                        // if blog not saved yet
                        binding.likeBtn.setImageResource(R.drawable.heart_white)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle any errors
                    Log.e("LIKE", "Error fetching Liked status", databaseError.toException())
                }
            })
        } else {
            Toast.makeText(this, "Failed to load blogs", Toast.LENGTH_SHORT).show()
        }
    }
}