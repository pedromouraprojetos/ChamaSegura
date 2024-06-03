package com.example.chamasegura

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configurando o OnClickListener para o botão "Registar"
        val registerButton = findViewById<Button>(R.id.registerButton)
        registerButton.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        // Configurando o OnClickListener para o TextView "Esqueceu a Palavra-Passe?"
        val forgotPasswordTextView = findViewById<TextView>(R.id.forgotPasswordTextView)
        forgotPasswordTextView.setOnClickListener {
            val intent = Intent(this, ForgotPassword::class.java)
            startActivity(intent)
        }

        // Configurando o OnClickListener para o botão "Entrar"
        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {
            val emailEditText = findViewById<EditText>(R.id.emailEditText)
            val passwordEditText = findViewById<EditText>(R.id.passwordEditText)

            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Verifica se as credenciais são válidas (para este exemplo, usamos credenciais padrão)
            if (email == "user@example.com" && password == "password") {
                val intent = Intent(this, HomePageUser::class.java)
                startActivity(intent)
                finish() // Opcional: finaliza a Activity atual para que o usuário não possa voltar
            } else {
                // Mostra uma mensagem de erro ou realiza outra ação apropriada
                emailEditText.error = "Email ou palavra-passe inválida"
                passwordEditText.error = "Email ou palavra-passe inválida"
            }
        }
    }
}
