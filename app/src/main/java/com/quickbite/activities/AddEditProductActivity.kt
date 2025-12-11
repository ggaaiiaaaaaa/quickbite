package com.quickbite.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.quickbite.databinding.ActivityAddEditProductBinding
import com.quickbite.models.Product
import java.util.UUID

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

        product = intent.getParcelableExtra("product", Product::class.java)

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
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
        val price = binding.etProductPrice.text.toString().toDoubleOrNull()
        val category = binding.etProductCategory.text.toString().trim()
        val nutritionalInfo = binding.etNutritionalInfo.text.toString().trim()
        val ingredients = binding.etIngredients.text.toString().split(",").map { it.trim() }
        val isAvailable = binding.swAvailability.isChecked

        if (name.isEmpty() || description.isEmpty() || price == null || category.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (imageUri == null && product == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            return
        }

        if (imageUri != null) {
            uploadImageAndSaveProduct(name, description, price, category, nutritionalInfo, ingredients, isAvailable)
        } else {
            saveProductToFirestore(product!!.imageUrl, name, description, price, category, nutritionalInfo, ingredients, isAvailable)
        }
    }

    private fun uploadImageAndSaveProduct(name: String, description: String, price: Double, category: String, nutritionalInfo: String, ingredients: List<String>, isAvailable: Boolean) {
        val storageRef = FirebaseStorage.getInstance().reference.child("product_images/${UUID.randomUUID()}")
        storageRef.putFile(imageUri!!)
            .addOnSuccessListener { 
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    saveProductToFirestore(uri.toString(), name, description, price, category, nutritionalInfo, ingredients, isAvailable)
                }
            }
            .addOnFailureListener { 
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveProductToFirestore(imageUrl: String, name: String, description: String, price: Double, category: String, nutritionalInfo: String, ingredients: List<String>, isAvailable: Boolean) {
        val db = FirebaseFirestore.getInstance()
        val collection = db.collection("products")

        val id = product?.id ?: collection.document().id
        val newProduct = Product(id, name, description, price, category, imageUrl, nutritionalInfo, ingredients, isAvailable)

        collection.document(id).set(newProduct)
            .addOnSuccessListener { 
                Toast.makeText(this, "Product saved!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { 
                Toast.makeText(this, "Failed to save product", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
