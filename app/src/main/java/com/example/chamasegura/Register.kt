package com.example.chamasegura

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chamasegura.retrofit.RetrofitClient
import com.example.chamasegura.retrofit.SupabaseCreateService
import com.example.chamasegura.retrofit.tabels.Users
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Register : AppCompatActivity() {
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.forgotpass)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)

        loginButton.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if (isValidPassword(password)) {
                        val user = Users(null, email, password, true, null, null, "ativo")
                        registerUser(user)
                    } else {
                        showError("A senha deve conter pelo menos 8 caracteres, uma letra maiúscula e um caractere especial")
                    }
                } else {
                    showError("Por favor, insira um email válido")
                }
            } else {
                showError("Por favor, preencha todos os campos")
                Log.e("Register", "Campos de email ou senha vazios")
            }
        }
    }

    private fun registerUser(user: Users) {
        val service = RetrofitClient.instance.create(SupabaseCreateService::class.java)

        service.addUsers(user).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@Register, "Registo bem-sucedido", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@Register, Login::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    showError("Erro no registo: $errorBody")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showError("Falha na conexão: ${t.message}")
            }
        })
    }

    private fun isValidPassword(password: String): Boolean {
        if (password.length < 8) {
            return false
        }

        val upperCasePattern = Regex("[A-Z]")
        if (!upperCasePattern.containsMatchIn(password)) {
            return false
        }

        val specialCharPattern = Regex("[!@#$%^&*(),.?\":{}|<>_]")
        if (!specialCharPattern.containsMatchIn(password)) {
            return false
        }

        return true
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        emailEditText.error = message
        passwordEditText.error = message
        Log.e("Register", message)
    }
}
