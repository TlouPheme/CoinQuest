package com.example.coinquest

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.coinquest.data.AppDatabase
import com.example.coinquest.data.Transaction
import kotlinx.coroutines.launch

/**
 * Allows the user to input a new financial transaction.
 * Features include title, amount, normalized category entry, and image attachment (e.g., receipts).
 */
class AddTransactionActivity : AppCompatActivity() {

    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        // Initialize UI components
        val etTitle = findViewById<EditText>(R.id.etTitle)
        val etAmount = findViewById<EditText>(R.id.etAmount)
        val etCategory = findViewById<EditText>(R.id.etCategory)
        val rgType = findViewById<RadioGroup>(R.id.rgType)
        val ivPreview = findViewById<ImageView>(R.id.ivTransactionImage)
        val btnPickImage = findViewById<Button>(R.id.btnPickImage)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)

        val database = AppDatabase.getDatabase(this)

        btnBack.setOnClickListener {
            finish()
        }

        /**
         * Image selection handling using the modern Activity Result API.
         * Allows users to pick a receipt or photo from their gallery.
         */
        val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                ivPreview.setImageURI(it)
                ivPreview.visibility = View.VISIBLE
            }
        }

        btnPickImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        /**
         * Save logic with validation and normalization.
         */
        btnSave.setOnClickListener {
            val title = etTitle.text.toString()
            val amount = etAmount.text.toString().toDoubleOrNull() ?: 0.0
            
            /** 
             * Category Normalization: Trims whitespace and ensures consistent casing (e.g., "food " -> "Food").
             * This ensures that aggregations in the summary screen work correctly.
             */
            val category = etCategory.text.toString().trim().lowercase().replaceFirstChar { it.uppercase() }
            val isIncome = rgType.checkedRadioButtonId == R.id.rbIncome

            // Validate mandatory fields
            if (title.isNotEmpty() && amount > 0 && category.isNotEmpty()) {
                val transaction = Transaction(
                    title = title,
                    amount = amount,
                    category = category,
                    date = System.currentTimeMillis(), // Record the exact time of entry
                    isIncome = isIncome,
                    imageUri = selectedImageUri?.toString()
                )

                // Async database insertion using Coroutines
                lifecycleScope.launch {
                    database.transactionDao().insert(transaction)
                    Toast.makeText(this@AddTransactionActivity, "Transaction Saved", Toast.LENGTH_SHORT).show()
                    finish() // Return to the previous screen (usually Dashboard)
                }
            } else {
                Toast.makeText(this, "Please enter valid title, amount, and category", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
