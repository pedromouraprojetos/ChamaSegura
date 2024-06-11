package com.example.chamasegura

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.chamasegura.retrofit.RetrofitClient
import com.example.chamasegura.retrofit.UpdateInfoUserService
import com.example.chamasegura.retrofit.UpdateUserRequest
import com.example.chamasegura.retrofit.UserInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.Intent

class Profile : AppCompatActivity() {

    private lateinit var firstName: EditText
    private lateinit var lastName: EditText
    private lateinit var username: EditText
    private lateinit var voltar: ImageButton
    private lateinit var avatar: ImageView
    private lateinit var save: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        firstName = findViewById(R.id.firstNameEditText)
        lastName = findViewById(R.id.lastNameEditText)
        username = findViewById(R.id.usernameEditText)
        voltar = findViewById(R.id.backButton)
        avatar = findViewById(R.id.avatarButton)
        save = findViewById(R.id.confirmButton)

        // Configurar o clique do botão "Voltar"
        voltar.setOnClickListener {
            // Iniciar uma nova atividade para retornar à tela inicial (home)
            val intent = Intent(this, HomePageUser::class.java)
            startActivity(intent)
            finish() // Opcional: finaliza esta atividade para que o usuário não possa retornar para ela pressionando o botão de voltar do dispositivo
        }

        save.setOnClickListener {
            val userEmail = UserInfo.getInstance().email
            val userUsername = username.text.toString().trim()
            val userFirstName = firstName.text.toString().trim()
            val userLastName = lastName.text.toString().trim()
            val fullName = "$userFirstName $userLastName"

            if (userEmail != null && userUsername.isNotEmpty() && userFirstName.isNotEmpty() && userLastName.isNotEmpty()) {
                updateUser(userEmail, userUsername, fullName)
            } else {
                Toast.makeText(this, "Todos os campos devem ser preenchidos", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateUser(email: String, username: String, name: String) {
        // Criação do serviço Retrofit
        val service = RetrofitClient.instance.create(UpdateInfoUserService::class.java)

        // Criação do objeto UpdateUserRequest com os dados a serem atualizados
        val updateUserRequest = UpdateUserRequest(username, name, false)  // Atribuição direta de `false` para 'firstEnter'

        // Chamada para o serviço de atualização usando o Retrofit
        service.updateUser("eq.$email", updateUserRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@Profile, "Informações atualizadas com sucesso", Toast.LENGTH_SHORT).show()
                } else {
                    // Logar o corpo da resposta de erro para ajudar no diagnóstico
                    val errorResponse = response.errorBody()?.string()
                    Toast.makeText(this@Profile, "Falha ao atualizar informações: ${response.code()} - $errorResponse", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@Profile, "Erro na conexão: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

}
