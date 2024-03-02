package com.example.blogapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.blogapp.Model.BlogItemModel
import com.example.blogapp.databinding.ActivityReadMoreBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ReadMoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReadMoreBinding
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadMoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backButton.setOnClickListener {
            finish()
        }

        val blogs = intent.getParcelableExtra<BlogItemModel>("blogItem")
        binding.saveBtn.setImageResource(R.drawable.save_articles_fill_red)

        if (blogs != null) {
            // Rerive user related data here e. x blog title etc.
            binding.titleText.text = blogs.heading
            binding.userName.text = blogs.userName
            binding.date.text = blogs.date
            binding.blogDescriptionTextView.text = blogs.post

            val userImageUrl = blogs.profileImage
            Glide.with(this)
                .load(userImageUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.profileImage)

            // Obtain the blogId
            val blogId = blogs.postId

            Toast.makeText(this, "${blogId.length}", Toast.LENGTH_SHORT).show()
            // Check if the blog is saved
            val userReference = databaseReference.child("blogs").child(blogId)

            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val isSaved = dataSnapshot.child("saved").getValue(Boolean::class.java) ?: false
                    // Update UI based on the value of isSaved
                    Toast.makeText(this@ReadMoreActivity, "$isSaved", Toast.LENGTH_SHORT).show()
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
        } else {
            Toast.makeText(this, "Failed to load blogs", Toast.LENGTH_SHORT).show()

        }
    }
}