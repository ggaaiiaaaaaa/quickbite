package com.quickbite.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.quickbite.databinding.ActivityAddEditProductBinding
import com.quickbite.models.Product

class AddEditProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditProductBinding
    private var product: Product? = null
    private var imageUri: Uri? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it
            binding.ivProductImage.setImageURI(imageUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        product = intent.getParcelableExtra("product")

        setupViews()
        setupClickListeners()
    }

    private fun setupViews() {
        setupAvailabilitySpinner()
        if (product != null) {
            supportActionBar?.title = "Edit Product"
            binding.etProductName.setText(product!!.name)
            binding.etProductDescription.setText(product!!.description)
            binding.etProductPrice.setText(product!!.price.toString())
            binding.etProductCategory.setText(product!!.category)
            binding.etNutritionalInfo.setText(product!!.nutritionalInfo)
            binding.etIngredients.setText(product!!.ingredients.joinToString(","))
            binding.swAvailability.isChecked = product!!.isAvailable

            Glide.with(this).load(product!!.imageUrl).into(binding.ivProductImage)
        } else {
            supportActionBar?.title = "Add Product"
        }
    }

    private fun setupAvailabilitySpinner() {
        val schedules = arrayOf("All Day", "Breakfast", "Lunch", "Dinner")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, schedules)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spAvailabilitySchedule.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.btnUploadImage.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.btnSaveProduct.setOnClickListener {
            saveProduct()
        }
    }

    private fun saveProduct() {
        val name = binding.etProductName.text.toString().trim()
        val description = binding.etProductDescription.text.toString().trim()
        val price = binding.etProductPrice.text.toString().trim()
        val category = binding.etProductCategory.text.toString().trim()

        if (name.isEmpty() || description.isEmpty() || price.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // TODO: Implement image upload to Firebase Storage and save product to Firestore
        Toast.makeText(this, "Product saved!", Toast.LENGTH_SHORT).show()
        finish()
    }
}
