package com.example.weatherapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            goToMain()
        }

        binding.registerButton.setOnClickListener {
            startActivity(
                Intent(this, RegisterActivity::class.java)
            )
        }

        binding.loginButton.setOnClickListener {
            login()
        }
    }

    private fun login() {

        val email = binding.email.text.toString().trim()
        val password = binding.password.text.toString()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.email.error = "Enter a valid email"
            return
        }

        if (password.length < 6) {
            binding.password.error =
                "Password must be at least 6 characters"
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.loginButton.isEnabled = false

        auth.signInWithEmailAndPassword(
            email,
            password
        ).addOnCompleteListener { task ->

            binding.progressBar.visibility = View.GONE
            binding.loginButton.isEnabled = true

            if (task.isSuccessful) {

                goToMain()

            } else {

                Toast.makeText(
                    this,
                    task.exception?.localizedMessage
                        ?: "Login failed",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun goToMain() {

        startActivity(
            Intent(this, MainActivity::class.java)
        )

        finish()
    }
}