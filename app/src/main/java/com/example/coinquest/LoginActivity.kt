package com.example.coinquest

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.coinquest.data.AppDatabase
import kotlinx.coroutines.launch

/**
 * Entry point for returning users.
 * Validates credentials against the local Room database.
 */
class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val loginBtn = findViewById<Button>(R.id.btnLogin)
        val registerBtn = findViewById<Button>(R.id.btnRegister)

        val database = AppDatabase.getDatabase(this)

        /**
         * Authentication Logic:
         * Queries the database for a user with the matching credentials.
         */
        loginBtn.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                lifecycleScope.launch {
                    val user = database.userDao().login(username, password)
                    if (user != null) {
                        // Success: Navigate to Dashboard
                        startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
                        finish() // Prevent coming back to login via back button
                    } else {
                        Toast.makeText(this@LoginActivity, getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, getString(R.string.please_fill_all_fields), Toast.LENGTH_SHORT).show()
            }
        }

        /** Navigation to Registration screen. */
        registerBtn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
