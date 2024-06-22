package com.example.chamasegura

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chamasegura.retrofit.RetrofitClient
import com.example.chamasegura.retrofit.SupabaseAuthService
import com.example.chamasegura.retrofit.SupabaseCreateService
import com.example.chamasegura.retrofit.tabels.Users
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateUserActivity : AppCompatActivity() {

    private lateinit var editTextName: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonCreateUser: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)

        editTextName = findViewById(R.id.editTextName)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonCreateUser = findViewById(R.id.buttonCreateUser)

        buttonCreateUser.setOnClickListener {
            val name = editTextName.text.toString().trim()
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (validateInputs(name, email, password)) {
                createUser(name, email, password)
            }
        }

        // Configuração do botão de voltar no canto superior esquerdo
        val imageViewBack: ImageView = findViewById(R.id.imageViewBack)
        imageViewBack.setOnClickListener {
            goToHomePageAdmin()
        }
    }

    private fun validateInputs(name: String, email: String, password: String): Boolean {
        if (name.isEmpty()) {
            showToast("Please enter a name.")
            return false
        }

        if (email.isEmpty()) {
            showToast("Please enter an email.")
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Please enter a valid email.")
            return false
        }

        if (password.length < 8) {
            showToast("Password must be at least 8 characters.")
            return false
        }

        if (!password.any { it.isUpperCase() }) {
            showToast("Password must contain at least one uppercase letter.")
            return false
        }

        if (!password.any { !it.isLetterOrDigit() }) {
            showToast("Password must contain at least one special character.")
            return false
        }

        // Verifying duplicate email and name
        if (isEmailOrNameDuplicate(email, name)) {
            showToast("Email or name already in use.")
            return false
        }

        return true
    }

    private fun isEmailOrNameDuplicate(email: String, name: String): Boolean {
        val service = RetrofitClient.instance.create(SupabaseAuthService::class.java)
        val call = service.getAllUsers()

        var isDuplicate = false

        call.enqueue(object : Callback<List<Users>> {
            override fun onResponse(call: Call<List<Users>>, response: Response<List<Users>>) {
                if (response.isSuccessful) {
                    val users = response.body() ?: emptyList()
                    isDuplicate = users.any { it.email == email || it.name == name }
                } else {
                    showToast("Error checking email or name duplication.")
                }
            }

            override fun onFailure(call: Call<List<Users>>, t: Throwable) {
                showToast("Error checking email or name duplication.")
            }
        })

        return isDuplicate
    }

    private fun createUser(name: String, email: String, password: String) {
        val service = RetrofitClient.instance.create(SupabaseCreateService::class.java)
        val user = Users(null, email, password, true, name, null, "Ativo", null)

        service.addUsers(user).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@CreateUserActivity, "Registration successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@CreateUserActivity, HomePageAdmin::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    showError("Registration error: $errorBody")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showError("Connection failed: ${t.message}")
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun goToHomePageAdmin() {
        val intent = Intent(this, HomePageAdmin::class.java)
        startActivity(intent)
        finish()
    }
}
