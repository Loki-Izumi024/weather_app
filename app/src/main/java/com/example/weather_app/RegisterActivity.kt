package com.example.weatherapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.registerButton.setOnClickListener {
            register()
        }
    }

    private fun register() {

        val email =
            binding.email.text.toString().trim()

        val password =
            binding.password.text.toString()

        val confirmPassword =
            binding.confirmPassword.text.toString()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.email.error =
                "Enter a valid email"
            return
        }

        if (password.length < 6) {
            binding.password.error =
                "Password must be at least 6 characters"
            return
        }

        if (password != confirmPassword) {
            binding.confirmPassword.error =
                "Passwords do not match"
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.registerButton.isEnabled = false

        auth.createUserWithEmailAndPassword(
            email,
            password
        ).addOnCompleteListener { task ->

            binding.progressBar.visibility = View.GONE
            binding.registerButton.isEnabled = true

            if (task.isSuccessful) {

                Toast.makeText(
                    this,
                    "Registration successful",
                    Toast.LENGTH_SHORT
                ).show()

                startActivity(
                    Intent(this, MainActivity::class.java)
                )

                finish()

            } else {

                Toast.makeText(
                    this,
                    task.exception?.localizedMessage
                        ?: "Registration failed",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}