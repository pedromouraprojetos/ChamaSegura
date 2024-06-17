package com.example.chamasegura

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chamasegura.retrofit.RetrofitClient
import com.example.chamasegura.retrofit.SupabaseAuthService
import com.example.chamasegura.retrofit.UpdateUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditUserActivity : AppCompatActivity() {

    private var userId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)


        userId = intent.getLongExtra("userId", -1L)
        Log.d("teste", "idUser: $userId")

        // Aqui você inicializa os EditTexts e o botão salvar
        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val editTextName = findViewById<EditText>(R.id.editTextName)

        val buttonSave = findViewById<Button>(R.id.buttonSave)

        buttonSave.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()
            val name = editTextName.text.toString().trim()

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
    }
}
