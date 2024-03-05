package com.example.blogapp

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.blogapp.Model.BlogItemModel
import com.example.blogapp.Model.UserData
import com.example.blogapp.databinding.ActivityAddBlogBinding
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.Circle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date

class AddBlogActivity : AppCompatActivity() {
    val binding: ActivityAddBlogBinding by lazy {
        ActivityAddBlogBinding.inflate(layoutInflater)
    }

    private val databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("blogs")
    private val userReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("users")

    lateinit var blogItem: BlogItemModel
    private val auth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        loader()
        binding.imageButton.setOnClickListener {
            finish()
        }

        binding.addBlogButton.setOnClickListener {

            val title = binding.blogTitle.editText?.text.toString().trim()
            val description = binding.blogDescription.text.toString().trim()

            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please Fill all the fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // get current user
            val user: FirebaseUser? = auth.currentUser
            binding.loaderAddBlog.visibility = View.VISIBLE
            binding.addBlogButton.visibility = View.INVISIBLE

            if (user != null) {
                val userId = user.uid
                val userName = user.displayName ?: "Anonymous"
                val userImageUrl = user.photoUrl ?: ""

                // fetch user name and user profile from database
                userReference.child(userId)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val userData = snapshot.getValue(UserData::class.java)
                            if (userData != null) {
                                val userNameFromDB = userData.name
                                val userImageUrlFromDB = userData.profileImage
                                val currentDate = SimpleDateFormat("yyyy-MM-dd").format(Date())

                                // create a blogItemModel
                                blogItem = BlogItemModel(
                                    title,
                                    userNameFromDB,
                                    currentDate,
                                    description,
                                    userId,
                                    0,
                                    userImageUrlFromDB
                                )
                                // generate a unique key for the blog post
                                val key = databaseReference.push().key
                                if (key != null) {
                                    blogItem.postId = key
                                    val blogReference = databaseReference.child(key)
                                    blogReference.setValue(blogItem).addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            binding.loaderAddBlog.visibility = View.GONE
                                            finish()
                                        } else {
                                            Toast.makeText(
                                                this@AddBlogActivity,
                                                "Failed to add blog",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })
            }
        }
    }

    private fun loader() {
        val progressBar = binding.loaderAddBlog as ProgressBar
        val circle: Sprite = Circle()
        progressBar.indeterminateDrawable = circle
    }
}