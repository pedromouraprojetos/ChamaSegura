package com.example.chamasegura

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chamasegura.retrofit.RetrofitClient
import com.example.chamasegura.retrofit.SupabaseAuthService
import com.example.chamasegura.retrofit.tabels.Queimadas
import com.example.chamasegura.retrofit.tabels.Users
import com.example.chamasegura.retrofit.tabels.Roles
import QueimadasAdapter
import android.content.Intent
import android.widget.ImageView
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.drawerlayout.widget.DrawerLayout

class NotificacoesUser : AppCompatActivity() {
    val service = RetrofitClient.instance.create(SupabaseAuthService::class.java)
    private lateinit var drawerLayout: DrawerLayout

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_notificacoes_user)
        drawerLayout = findViewById(R.id.notificacoes)

        //Bottom Menu

        findViewById<ImageView>(R.id.fire_icon).setOnClickListener {
            val intent = Intent(this, HomePageUser::class.java)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.notification_icon).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        findViewById<ImageView>(R.id.profile_icon).setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.menu_icon).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_logout -> {
                    logout()
                    true
                }
                else -> false
            }
        }

        //recyclerView = findViewById(R.id.recycler_view_queimadas)
        //recyclerView.layoutManager = LinearLayoutManager(this)

        val idUser = MyApp.userId.toLong()

        if (idUser == 0L) {
            Toast.makeText(this, "Erro: ID do usuário inválido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        Log.d("teste", "idUser: $idUser")
        service.getRoleIdByUserId("eq.$idUser").enqueue(object : Callback<List<Users>> {
            override fun onResponse(call: Call<List<Users>>, response: Response<List<Users>>) {
                if (response.isSuccessful) {
                    val usersList = response.body()
                    if (usersList != null && usersList.isNotEmpty()) {
                        val user = usersList[0]
                        val idRole = user.idRole
                        Log.d("Role", "idRole: $idRole")
                        if (idRole != null) {
                            val idRoleString = idRole.toString()
                            service.getRole("eq.$idRoleString").enqueue(object : Callback<List<Roles>> {
                                override fun onResponse(call: Call<List<Roles>>, response: Response<List<Roles>>) {
                                    if (response.isSuccessful) {
                                        val rolesList = response.body()
                                        if (rolesList != null && rolesList.isNotEmpty()) {
                                            val role = rolesList[0]
                                            val roleType = role.type.toString()
                                            Log.d("Role", "Tipo de role: $roleType")
                                            if (roleType in listOf("Admin", "Bombeiros", "Proteção Civil", "Municipio")) {
                                                //fetchPendingQueimadas()
                                            }
                                        } else {
                                            Toast.makeText(this@NotificacoesUser, "Tipo de role não encontrado", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        Toast.makeText(this@NotificacoesUser, "Erro ao obter o tipo de role", Toast.LENGTH_SHORT).show()
                                        Log.e("teste", "Falha ao obter tipo do role do user: ${response.code()} - ${response.errorBody()?.string()}")
                                    }
                                }

                                override fun onFailure(call: Call<List<Roles>>, t: Throwable) {
                                    val errorMessage = t.message ?: "Erro desconhecido"
                                    Log.d("Erro", "Falha ao obter o tipo de role: $errorMessage")
                                    Toast.makeText(this@NotificacoesUser, "Erro ao obter o tipo de role: $errorMessage", Toast.LENGTH_SHORT).show()
                                }
                            })
                        } else {
                            Toast.makeText(this@NotificacoesUser, "ID do role do usuário não encontrado", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@NotificacoesUser, "Usuário não encontrado", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@NotificacoesUser, "Erro ao obter o ID do role do usuário", Toast.LENGTH_SHORT).show()
                    Log.e("teste", "Falha ao obter id do role do user: ${response.code()} - ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<Users>>, t: Throwable) {
                val errorMessage = t.message ?: "Erro desconhecido"
                Log.d("Erro", "Falha ao obter o ID do role do usuário: $errorMessage")
                Toast.makeText(this@NotificacoesUser, "Erro ao obter o ID do role do usuário: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        })

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.notificacoes)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun fetchPendingQueimadas() {
        service.getAllPendingQueimadas().enqueue(object : Callback<List<Queimadas>> {
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
                    Toast.makeText(this@NotificacoesUser, "Erro ao buscar queimadas pendentes", Toast.LENGTH_SHORT).show()
                    updatePendingQueimadasUI(emptyList())
                }
            }

            override fun onFailure(call: Call<List<Queimadas>>, t: Throwable) {
                Toast.makeText(this@NotificacoesUser, "Falha na solicitação: ${t.message}", Toast.LENGTH_SHORT).show()
                updatePendingQueimadasUI(emptyList())
            }
        })
    }

    private fun updatePendingQueimadasUI(queimadas: List<Queimadas>) {
        val adapter = QueimadasAdapter(queimadas)
        //recyclerView.adapter = adapter
    }

    private fun logout() {
        val intent = Intent(this, Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
