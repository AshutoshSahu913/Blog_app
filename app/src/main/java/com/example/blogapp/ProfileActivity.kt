package com.example.blogapp

import android.app.Dialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import coil.load
import com.example.blogapp.databinding.ActivityProfileBinding
import com.example.blogapp.register.WelcomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileActivity : AppCompatActivity() {
    private val binding: ActivityProfileBinding by lazy {
        ActivityProfileBinding.inflate(layoutInflater)
    }

    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        // to go add article page
        binding.addNewBlogButton.setOnClickListener {
            startActivity(Intent(this, AddBlogActivity::class.java))
        }
        // to go to Your Article activity
        binding.articlesButton.setOnClickListener {
            startActivity(Intent(this, ArticleActivity::class.java))
        }

        // to logOut
        binding.logOutButton.setOnClickListener {
            auth.signOut()
            // navigate
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        }


        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference.child("users")
        val userId = auth.currentUser?.uid
        if (userId != null) {
            loadUserProfileData(userId)
        }


        //open dialog for profile
        binding.userProfile.setOnLongClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_image)
            //set transparent background of dialog
            //set profile image in the dialog box show in full length
            val image = dialog.findViewById<ImageView>(R.id.image)
            val imageObject = binding.userProfile.drawable
            image.setImageDrawable(imageObject)

            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            // set dialog parameter height and width
            val lp = WindowManager.LayoutParams()
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT
            lp.height = WindowManager.LayoutParams.MATCH_PARENT

            //blur background of dialog
            lp.flags = WindowManager.LayoutParams.FLAG_BLUR_BEHIND
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                lp.blurBehindRadius = 10
            }

            //set all attributes to dialog
            dialog.window?.attributes = lp
            dialog.show()
            true
        }
    }

    private fun loadUserProfileData(userId: String) {
        val userReference = databaseReference.child(userId)

        // load user profile Image
        userReference.child("profileImage").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val profileImageUrl = snapshot.getValue(String::class.java)
                if (profileImageUrl != null) {
                    binding.userProfile.load(profileImageUrl) {
                        placeholder(R.drawable.dog)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@ProfileActivity,
                    "Failed to load user image 🙃",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })


        // load user name
        userReference.child("name").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userName = snapshot.getValue(String::class.java)
                if (userName != null) {
                    binding.userName.text = userName
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}