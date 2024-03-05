package com.example.blogapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.blogapp.Model.BlogItemModel
import com.example.blogapp.adapter.BlogAdapter
import com.example.blogapp.databinding.ActivityMainBinding
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.Circle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var databaseReference: DatabaseReference
    private val blogItems = mutableListOf<BlogItemModel>()
    private lateinit var auth: FirebaseAuth
    lateinit var blogAdapter: BlogAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        loader()
        binding.loader.visibility = View.VISIBLE

        // to go save article page
        binding.saveArticalButton.setOnClickListener {
            startActivity(Intent(this, SavedArticlesActivity::class.java))
        }
        // to go profile activity
        binding.profileImages.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }// to go profile activity
        binding.cardView2.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference.child("blogs")
        val userId = auth.currentUser?.uid

        // set user profile
        if (userId != null) {
            loadUserProfileImage(userId)
        }

        // Initialize the recycler view and set adapter
        val recyclerView = binding.blogRecyclerView
        blogAdapter = BlogAdapter(blogItems)
        recyclerView.adapter = blogAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)


        // fetch data from firebase database
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                blogItems.clear()
                for (snapshot1 in snapshot.children) {
                    binding.loader.visibility = View.GONE
                    val blogItem = snapshot1.getValue(BlogItemModel::class.java)
                    if (blogItem != null) {
                        binding.emptyImg.visibility = View.GONE
                        blogItems.add(blogItem)
                    }
                }
                if (blogItems.isEmpty()) {
                    binding.emptyImg.visibility = View.VISIBLE
                }
                // revers the list
                blogItems.reverse()

                // Notify the adapter that the data has changed
                blogAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Blog loading field", Toast.LENGTH_SHORT).show()
            }
        })


        binding.floatingAddArticleButton.setOnClickListener {
            startActivity(Intent(this, AddBlogActivity::class.java))
        }

        // for search the element in the ContactList
        binding.searchBlog.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s == null) {
                    blogAdapter.items = blogItems
                    blogAdapter.notifyDataSetChanged()
                    Toast.makeText(this@MainActivity, "null condition", Toast.LENGTH_SHORT).show()
                } else {
                    if (s.length == 0 || s.isNullOrBlank() || s.isNullOrEmpty()) {
                        blogAdapter.items = blogItems
                        blogAdapter.notifyDataSetChanged()

                        Toast.makeText(
                            this@MainActivity,
                            "No Data Found! 😵‍💫",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {

                        val tempList = ArrayList<BlogItemModel>()
                        blogItems.forEach {
                            if (it.userName != null) {
                                if (it.userName.contains(
                                        s,
                                        ignoreCase = true
                                    ) || it.heading!!.contains(s, ignoreCase = true)
                                ) {
                                    tempList.add(it)
                                }
                            }
                        }
                        blogAdapter.items = tempList
                        blogAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }


    private fun loadUserProfileImage(userId: String) {
        val userReference = FirebaseDatabase.getInstance().reference.child("users").child(userId)
        /*if code not working uncomment this line
        val userReference = FirebaseDatabase.getInstance("https://blog-app-147b1-default-rtdb.asia-southeast1.firebasedatabase.app").reference.child("users").child(userId)*/

        userReference.child("profileImage").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val profileImageUrl = snapshot.getValue(String::class.java)

                if (profileImageUrl != null) {
                    binding.profileImages.load(profileImageUrl) {
                        placeholder(R.drawable.dog)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error loading profile image", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }


    private fun loader() {
        // code for loader
        val progressBar = binding.loader as ProgressBar
        val cubeGrid: Sprite = Circle()
        progressBar.indeterminateDrawable = cubeGrid
    }

}