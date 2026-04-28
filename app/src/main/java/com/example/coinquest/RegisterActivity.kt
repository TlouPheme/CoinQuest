package com.example.coinquest

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.coinquest.data.AppDatabase
import com.example.coinquest.data.User
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val createAccountBtn = findViewById<Button>(R.id.btnCreateAccount)

        val database = AppDatabase.getDatabase(this)

        createAccountBtn.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                lifecycleScope.launch {
                    val existingUser = database.userDao().getUserByUsername(username)
                    if (existingUser == null) {
                        database.userDao().registerUser(User(username, password))
                        Toast.makeText(this@RegisterActivity, getString(R.string.registration_successful), Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@RegisterActivity, getString(R.string.registration_failed), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, getString(R.string.please_fill_all_fields), Toast.LENGTH_SHORT).show()
            }
        }
    }
}
