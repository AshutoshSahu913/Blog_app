package com.example.blogapp.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.example.blogapp.MainActivity
import com.example.blogapp.R
import com.example.blogapp.databinding.ActivityLoginBinding
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.Circle
import com.github.ybq.android.spinkit.style.CubeGrid
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class LoginActivity : AppCompatActivity() {

    val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        binding.loginButton.setOnClickListener {
            binding.loginEmailAddress.setBackgroundResource(R.drawable.edit_text_blue_shape)
            binding.loginPassword.setBackgroundResource(R.drawable.edit_text_blue_shape)

            val loginEmail = binding.loginEmailAddress.text.toString()
            val loginPassword = binding.loginPassword.text.toString()

            if (loginEmail.isEmpty() || loginPassword.isEmpty()) {
                if (loginEmail.isEmpty()) {
                    binding.loginEmailAddress.setBackgroundResource(R.drawable.edit_text_red_shape)
                }
                if (loginPassword.isEmpty()) {
                    binding.loginPassword.setBackgroundResource(R.drawable.edit_text_red_shape)
                }
                Toast.makeText(this, "Please Fill All The Details", Toast.LENGTH_SHORT).show()
            } else {
                loader()
                binding.loginButton.visibility = View.INVISIBLE
                binding.loaderLogin.visibility = View.VISIBLE
                auth.signInWithEmailAndPassword(loginEmail, loginPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Login Successful 😁", Toast.LENGTH_SHORT)
                                .show()
                            binding.loaderLogin.visibility = View.GONE
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "Login Field. Please Enter correct Details",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.loaderLogin.visibility = View.GONE
                        }
                    }
            }

        }
        binding.registerButton.setOnClickListener {
            startActivity(Intent(this, SignInAndRegistrationActivity::class.java))
            finish()
        }
    }

    fun loader() {
        // code for loader
        val progressBar = binding.loaderLogin as ProgressBar
        val circle: Sprite = Circle()
        progressBar.indeterminateDrawable = circle

    }
}