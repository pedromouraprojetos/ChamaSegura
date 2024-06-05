// HomePageUser.kt
package com.example.chamasegura

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chamasegura.retrofit.RetrofitClient
import com.example.chamasegura.retrofit.SupabaseAuthService
import com.example.chamasegura.retrofit.tabels.Queimadas
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomePageUser : AppCompatActivity() {
    private lateinit var drawerLayout: androidx.drawerlayout.widget.DrawerLayout
    private var idUser: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_page_user)

        drawerLayout = findViewById(R.id.drawer_layout)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.navigation_home)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Recebe os dados do Intent
        val firstName = intent.getStringExtra("firstName") ?: "null"
        idUser = intent.getLongExtra("idUser", 0)
        if (idUser == 0L) {
            Toast.makeText(this, "Erro: ID do usuário inválido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Atualiza a saudação
        val greetingMessage = findViewById<TextView>(R.id.greeting_message)
        greetingMessage.text = "Olá $firstName!"

        // Recebe os dados da queimada criada (se houver)
        val queimadaDate = intent.getStringExtra("queimadaDate")
        val queimadaStatus = intent.getStringExtra("queimadaStatus")

        // Atualiza a interface com os dados da queimada criada
        if (queimadaDate != null && queimadaStatus != null) {
            val queimadaInfoTextView = findViewById<TextView>(R.id.queimada_info)
            queimadaInfoTextView.text = "Queimada Criada em $queimadaDate com status: $queimadaStatus"
        }

        // Configurar a navegação
        findViewById<ImageView>(R.id.notification_icon).setOnClickListener {
            val intent = Intent(this, NotificacoesUser::class.java)
            intent.putExtra("firstName", firstName)
            intent.putExtra("idUser", idUser)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.menu_icon).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        findViewById<ImageView>(R.id.fire_icon).setOnClickListener {
            // Já está na HomePageUser, então não precisa de ação aqui.
        }

        findViewById<ImageView>(R.id.profile_icon).setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            intent.putExtra("firstName", firstName)
            intent.putExtra("idUser", idUser) // Certifique-se de passar o idUser aqui também
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.fab).setOnClickListener {
            val intent = Intent(this, createQueimada::class.java)
            intent.putExtra("firstName", firstName)
            intent.putExtra("idUser", idUser) // Certifique-se de passar o idUser aqui também
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.menu_icon).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_logout -> {
                    // Perform logout logic here
                    logout()
                    true
                }
                else -> false
            }
        }

        // Chamada da API para obter as queimadas pendentes
        fetchPendingQueimadas(idUser)
    }

    private fun fetchPendingQueimadas(idUser: Long) {
        val service = RetrofitClient.instance.create(SupabaseAuthService::class.java)
        Log.e("home", "idUser: $idUser")

        service.getPendingQueimadas("eq.$idUser").enqueue(object : Callback<List<Queimadas>> {
            override fun onResponse(call: Call<List<Queimadas>>, response: Response<List<Queimadas>>) {

                if (response.isSuccessful) {
                    val queimadas = response.body()
                    if (!queimadas.isNullOrEmpty()) {
                        updatePendingQueimadasUI(queimadas)
                    } else {
                        Log.e("home", "Resposta vazia ou nula")
                        updatePendingQueimadasUI(emptyList())
                    }
                } else {
                    Log.e("home", "Erro na resposta da API: ${response.code()}")
                    val errorBody = response.errorBody()?.string()
                    Log.e("home", "Error body: $errorBody")
                    Toast.makeText(this@HomePageUser, "Erro ao buscar queimadas pendentes", Toast.LENGTH_SHORT).show()
                    updatePendingQueimadasUI(emptyList())
                }
            }


            override fun onFailure(call: Call<List<Queimadas>>, t: Throwable) {
                Toast.makeText(this@HomePageUser, "Falha na solicitação: ${t.message}", Toast.LENGTH_SHORT).show()
                updatePendingQueimadasUI(emptyList())
            }
        })
    }

    private fun updatePendingQueimadasUI(queimadas: List<Queimadas>) {
        val queimadaInfoTextView = findViewById<TextView>(R.id.queimada_info)
        val sb = StringBuilder()
        if (queimadas.isEmpty()) {
            Log.d("HomePageUser", "Nenhuma queimada pendente")
            sb.append("Não tem Queimadas Pendentes")
        } else {
            for (queimada in queimadas) {
                sb.append("Data: ${queimada.date} Estado: ${queimada.status}\n")
            }
        }
        Log.d("HomePageUser", "Texto definido no TextView: $sb")
        queimadaInfoTextView.text = sb.toString()
    }


    private fun logout() {
        // Clear user session and navigate to login screen
        val intent = Intent(this, Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
