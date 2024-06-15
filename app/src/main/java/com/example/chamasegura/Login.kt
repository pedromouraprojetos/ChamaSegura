package com.example.chamasegura


import MyApp
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chamasegura.retrofit.RetrofitClient
import com.example.chamasegura.retrofit.SupabaseAuthService
import com.example.chamasegura.retrofit.UserInfo
import com.example.chamasegura.retrofit.tabels.Users
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Login : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var forgotPasswordTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView)

        registerButton.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        forgotPasswordTextView.setOnClickListener {
            val intent = Intent(this, ForgotPassword::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                showError("Por favor, preencha todos os campos")
                Log.e("Login", "Campos de email ou senha vazios")
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        val service = RetrofitClient.instance.create(SupabaseAuthService::class.java)

        service.verifyUser("eq.$email", "eq.$password").enqueue(object : Callback<List<Users>> {
            override fun onResponse(call: Call<List<Users>>, response: Response<List<Users>>) {
                if (response.isSuccessful) {
                    val users = response.body()
                    if (users != null && users.isNotEmpty()) {
                        // Sucesso na verificação das credenciais
                        val user = users[0]

                        val userInfo = UserInfo.getInstance()
                        userInfo.email = user.email
                        userInfo.idRole = user.idRole

                        if(user.idRole?.toInt() == 1) {
                            val intent = Intent(this@Login, HomePageAdmin::class.java)
                            MyApp.userId = user.idUsers.toString()
                            MyApp.firstName = "admin"
                            startActivity(intent)
                        }else if (user.FirstEnter==true) {
                            // Ação se firstEnter for true
                            val intent = Intent(this@Login, Profile::class.java)
                            MyApp.userId = user.idUsers.toString()
                            MyApp.firstName = user.name.toString()
                            startActivity(intent)
                        } else {
                            // Ação se firstEnter for false
                            val intent = Intent(this@Login, HomePageUser::class.java)
                            MyApp.userId = user.idUsers.toString()
                            MyApp.firstName = user.name.toString()
                            startActivity(intent)
                        }

                        Toast.makeText(this@Login, "Login bem-sucedido", Toast.LENGTH_SHORT).show()
                    } else {
                        showError("Email ou palavra-passe inválida")
                        Log.e("Login", "Falha na verificação das credenciais: usuário não encontrado")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    showError("Email ou palavra-passe inválida")
                    Log.e("Login", "Falha na verificação das credenciais: $errorBody")
                }
            }

            override fun onFailure(call: Call<List<Users>>, t: Throwable) {
                showError("Falha na conexão: ${t.message}")
                Log.e("Login", "Falha na conexão", t)
            }
        })
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        emailEditText.error = message
        passwordEditText.error = message
        Log.e("Login", message)
    }
}
