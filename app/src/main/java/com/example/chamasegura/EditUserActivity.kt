package com.example.chamasegura

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chamasegura.retrofit.RetrofitClient
import com.example.chamasegura.retrofit.SupabaseAuthService
import com.example.chamasegura.retrofit.UpdateUser
import com.example.chamasegura.retrofit.tabels.Users
import com.example.chamasegura.retrofit.tabels.Roles
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditUserActivity : AppCompatActivity() {

    private var userId: Long = -1L
    private var userRole: Long = -1L
    private lateinit var currentUserEmail: String
    private lateinit var editTextName: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var spinnerCargo: Spinner
    private var roles: List<Roles> = emptyList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)

        userId = intent.getLongExtra("userId", -1L)
        val userName = intent.getStringExtra("userName")
        val userEmail = intent.getStringExtra("userEmail")
        userRole = intent.getLongExtra("userRole", -1L)
        Log.d("teste", "idUser: $userId, idRole: $userRole")

        // Inicializa os EditTexts e o Spinner
        editTextName = findViewById(R.id.editTextName)
        editTextEmail = findViewById(R.id.editTextEmail)
        spinnerCargo = findViewById(R.id.spinnerRole)

        // Popula os EditTexts com os dados recebidos do Intent
        editTextName.setText(userName)
        editTextEmail.setText(userEmail)

        // Configura o Spinner
        fetchCargos()

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
            val name = editTextName.text.toString().trim()
            val selectedRolePosition = spinnerCargo.selectedItemPosition

            if (validateFields(email, name)) {
                // Verifica se o email ou nome já existem na base de dados
                updateUser(email, name, roles[selectedRolePosition].idRole)
            } else {
                Toast.makeText(applicationContext, "Por favor, preencha todos os campos corretamente.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun fetchCargos() {
        val service = RetrofitClient.instance.create(SupabaseAuthService::class.java)
        val call = service.getAllCargos()

        call.enqueue(object : Callback<List<Roles>> {
            override fun onResponse(call: Call<List<Roles>>, response: Response<List<Roles>>) {
                if (response.isSuccessful) {
                    roles = response.body() ?: emptyList() // Atualiza a lista de roles com os dados recebidos
                    setupSpinner(roles)
                } else {
                    showToast("Erro ao buscar cargos.")
                }
            }

            override fun onFailure(call: Call<List<Roles>>, t: Throwable) {
                showToast("Erro ao buscar cargos: ${t.message}")
            }
        })
    }


    private fun setupSpinner(roles: List<Roles>) {
        val roleNames = roles.map { it.type }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roleNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCargo.adapter = adapter

        // Define o cargo atual do usuário no Spinner
        val cargoPosition = roles.indexOfFirst { it.idRole == userRole }
        if (cargoPosition >= 0) {
            spinnerCargo.setSelection(cargoPosition)
        }
    }

    private fun fetchCurrentUserEmail() {
        currentUserEmail = "email_atual@exemplo.com" // Você pode implementar a lógica para obter o email atual do usuário aqui
    }

    private fun validateFields(email: String,name: String): Boolean {
        if (email.isEmpty()  || name.isEmpty()) {
            return false
        }

        return true
    }

    private fun updateUser(email: String, name: String, idRole: Long) {
        val userId2 = userId.toString()
        val updateUser = UpdateUser(email,name, idRole)

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
