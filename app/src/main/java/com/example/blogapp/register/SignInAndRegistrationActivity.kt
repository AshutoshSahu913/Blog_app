package com.example.blogapp.register

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.constraintlayout.widget.Placeholder
import coil.load
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.blogapp.Model.UserData
import com.example.blogapp.R
import com.example.blogapp.databinding.ActivitySignInAndRegistrationBinding
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.Circle
import com.github.ybq.android.spinkit.style.CubeGrid
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class SignInAndRegistrationActivity : AppCompatActivity() {
    private val binding: ActivitySignInAndRegistrationBinding by lazy {
        ActivitySignInAndRegistrationBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
//        /*if code not working uncomment this line
//                database = FirebaseDatabase.getInstance("https://blog-app-147b1-default-rtdb.asia-southeast1.firebasedatabase.app")
        storage = FirebaseStorage.getInstance()

        binding.registerButton.setOnClickListener {
            binding.registerName.setBackgroundResource(R.drawable.edit_text_blue_shape)
            binding.registerEmail.setBackgroundResource(R.drawable.edit_text_blue_shape)
            binding.registerPassword.setBackgroundResource(R.drawable.edit_text_blue_shape)
            // Get data from edit text field
            val registerName = binding.registerName.text.toString()
            val registerEmail = binding.registerEmail.text.toString()
            val registerPassword = binding.registerPassword.text.toString()
            if (registerName.isEmpty() || registerEmail.isEmpty() || registerPassword.isEmpty()) {

                if (registerName.isEmpty()) {
                    binding.registerName.requestFocus()
                    binding.registerName.setBackgroundResource(R.drawable.edit_text_red_shape)
                }
                if (registerEmail.isEmpty()) {
                    binding.registerEmail.requestFocus()
                    binding.registerEmail.setBackgroundResource(R.drawable.edit_text_red_shape)
                }
                if (registerPassword.isEmpty()) {
                    binding.registerPassword.requestFocus()
                    binding.registerPassword.setBackgroundResource(R.drawable.edit_text_red_shape)
                }
                Toast.makeText(this, "Please Fill All The Details", Toast.LENGTH_SHORT).show()
            } else {
                loader()
                binding.registerButton.visibility = View.INVISIBLE
                binding.loaderRegister.visibility = View.VISIBLE

                auth.createUserWithEmailAndPassword(registerEmail, registerPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            auth.signOut()
                            user?.let {
                                // Save user data in to Firebase realtime database
                                val userReference = database.getReference("users")
                                val userId = user.uid
                                val userData = UserData(
                                    registerName, registerEmail
                                )
                                userReference.child(userId).setValue(userData)
                                // upload image to firebase storage
                                val storageReference =
                                    storage.reference.child("profile_image/$userId.jpg")
                                storageReference.putFile(imageUri!!).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        storageReference.downloadUrl.addOnCompleteListener { imageUri ->
                                            if (imageUri.isSuccessful) {
                                                val imageUrl = imageUri.result.toString()
                                                // save the image url to the realtime database
                                                userReference.child(userId)
                                                    .child("profileImage").setValue(imageUrl)
                                            }
                                        }
                                    }
                                }
                                Toast.makeText(
                                    this, "User Register Successfully", Toast.LENGTH_SHORT
                                ).show()
                                binding.loaderRegister.visibility = View.GONE
                                startActivity(Intent(this, LoginActivity::class.java))
                                finish()
                            }
                        } else {
                            Toast.makeText(this, "User Registration Failed", Toast.LENGTH_SHORT)
                                .show()
                            binding.loaderRegister.visibility = View.GONE
                        }
                    }
            }
        }

//        }

        // set on click listener for the Choose image
        binding.cardView.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "select Image"), PICK_IMAGE_REQUEST)
        }

        binding.loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) imageUri =
            data.data
//        Glide.with(this).load(imageUri).apply(RequestOptions.circleCropTransform())
//            .into(binding.registerUserImage)

        binding.registerUserImage.load(imageUri) {
            placeholder(R.drawable.image)
        }
    }

    private fun loader() {
        // code for loader
        val progressBar = binding.loaderRegister as ProgressBar
        val circle: Sprite = Circle()
        progressBar.indeterminateDrawable = circle

    }
}