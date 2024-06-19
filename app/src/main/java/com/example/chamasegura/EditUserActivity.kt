package com.example.chamasegura

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chamasegura.retrofit.RetrofitClient
import com.example.chamasegura.retrofit.SupabaseAuthService
import com.example.chamasegura.retrofit.UpdateUser
import com.example.chamasegura.retrofit.tabels.Users
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditUserActivity : AppCompatActivity() {

    private var userId: Long = -1L
    private lateinit var currentUserEmail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)

        userId = intent.getLongExtra("userId", -1L)
        Log.d("teste", "idUser: $userId")

        // Inicializa os EditTexts e o botão salvar
        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val editTextName = findViewById<EditText>(R.id.editTextName)

        val buttonSave = findViewById<Button>(R.id.buttonSave)

        // Obtém o email atual do usuário para comparação posterior
        fetchCurrentUserEmail()

        // Configuração da seta para voltar à HomePageAdmin
        val imageViewArrow = findViewById<ImageView>(R.id.imageViewArrow)
        imageViewArrow.setOnClickListener {
            val intent = Intent(this, HomePageAdmin::class.java)
            startActivity(intent)
            finish() // Finaliza a activity atual ao voltar para HomePageAdmin
        }

        buttonSave.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()
            val name = editTextName.text.toString().trim()

            if (validateFields(email, password, name)) {
                // Verifica se o email ou nome já existem na base de dados
                isEmailOrNameDuplicate(email, name) { isDuplicate ->
                    if (!isDuplicate) {
                        // Se não houver duplicidade, atualiza o usuário
                        updateUser(email, password, name)
                    } else {
                        // Se houver duplicidade, mostra mensagem de erro
                        Toast.makeText(applicationContext, "Email ou nome já existem na base de dados.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(applicationContext, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchCurrentUserEmail() {
        currentUserEmail = "email_atual@exemplo.com" // Você pode implementar a lógica para obter o email atual do usuário aqui
    }

    private fun validateFields(email: String, password: String, name: String): Boolean {
        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            return false
        }

        if (password.length < 8) {
            return false
        }

        if (!password.any { it.isUpperCase() }) {
            return false
        }

        val specialChars = setOf('!', '@', '#', '$', '%', '^', '&', '*')
        if (!password.any { it in specialChars }) {
            return false
        }

        return true
    }

    private fun isEmailOrNameDuplicate(email: String, name: String, callback: (Boolean) -> Unit) {
        val service = RetrofitClient.instance.create(SupabaseAuthService::class.java)
        val call = service.getAllUsers()

        call.enqueue(object : Callback<List<Users>> {
            override fun onResponse(call: Call<List<Users>>, response: Response<List<Users>>) {
                if (response.isSuccessful) {
                    val users = response.body() ?: emptyList()
                    val isDuplicate = users.any { it.email == email || it.name == name }
                    callback(isDuplicate)
                } else {
                    showToast("Error checking email or name duplication.")
                    callback(false)
                }
            }

            override fun onFailure(call: Call<List<Users>>, t: Throwable) {
                showToast("Error checking email or name duplication.")
                callback(false)
            }
        })
    }

    private fun updateUser(email: String, password: String, name: String) {
        val userId2 = userId.toString()
        val updateUser = UpdateUser(email, password, name)

        // Chama a função de atualização do Retrofit
        val service = RetrofitClient.instance.create(SupabaseAuthService::class.java)
        service.updateUser("eq.$userId2", updateUser).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // Atualização bem-sucedida
                    Toast.makeText(applicationContext, "Usuário atualizado com sucesso", Toast.LENGTH_SHORT).show()
                    finish() // Fecha a activity após a atualização
                } else {
                    // Erro na atualização
                    Toast.makeText(applicationContext, "Erro ao atualizar o usuário", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Falha na requisição
                Toast.makeText(applicationContext, "Falha na atualização do usuário: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("EditUserActivity", "Falha na requisição de atualização", t)
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }
}
