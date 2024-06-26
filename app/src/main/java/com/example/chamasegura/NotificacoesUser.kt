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
import QueimadasAdapter2
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
    private lateinit var navigationView: NavigationView
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_notificacoes_user)
        drawerLayout = findViewById(R.id.notificacoes)
        navigationView = findViewById(R.id.nav_view)

        getUserType { userType ->
            if (userType != null) {
                when (userType) {
                    "Admin" -> navigationView.inflateMenu(R.menu.drawer_menu_admin)
                    "user" -> navigationView.inflateMenu(R.menu.drawer_menu)
                    "Bombeiros" -> navigationView.inflateMenu(R.menu.drawer_menu_bombeiro)
                    "Proteção Civil" -> navigationView.inflateMenu(R.menu.drawer_menu_protecao_civil)
                    "Municipio" -> navigationView.inflateMenu(R.menu.drawer_menu_municipio)
                }
                getUser(MyApp.userId.toString()) { user ->
                    fetchPendingQueimadas(userType, user)
                }
            } else {
                Toast.makeText(this, "Erro ao obter o tipo de usuário", Toast.LENGTH_SHORT).show()
            }
        }

        // Bottom Menu

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
                R.id.nav_create_rules -> {
                    startActivity(Intent(this, CreateRules::class.java))
                    true
                }

                R.id.nav_logout -> {
                    logout()
                    true
                }

                else -> false
            }
        }

        recyclerView = findViewById(R.id.recycler_view_queimadas)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val idUser = MyApp.userId.toLong()
        if (idUser == 0L) {
            Toast.makeText(this, "Erro: ID do user inválido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        Log.d("teste", "idUser: $idUser")
    }

    private fun fetchPendingQueimadas(roleType: String, user: Users) {
        service.getQueimadasByStatus("eq.Pendente").enqueue(object : Callback<List<Queimadas>> {
            override fun onResponse(
                call: Call<List<Queimadas>>,
                response: Response<List<Queimadas>>
            ) {
                if (response.isSuccessful) {
                    val queimadas = response.body()
                    if (!queimadas.isNullOrEmpty()) {
                        // Filtra as queimadas pelo idMunicipalaties do user logado
                        val filteredQueimadas =
                            queimadas.filter { it.idMunicipalities?.toLongOrNull() == user.idMunicipalities }

                        // Verifica se o tipo de role permite o acesso
                        if (roleType in listOf(
                                "Admin",
                                "Bombeiros",
                                "Proteção Civil",
                                "Municipio"
                            )
                        ) {
                            updatePendingQueimadasUI(filteredQueimadas, roleType)
                        } else {
                            // Lida com o caso em que o tipo de usuário não tem acesso
                            // Pode exibir uma mensagem de erro ou fazer outra ação
                            Toast.makeText(
                                this@NotificacoesUser,
                                "Acesso não autorizado",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Log.e("home", "Resposta vazia ou nula")
                        updatePendingQueimadasUI(emptyList(), roleType)
                    }
                } else {
                    Log.e("home", "Erro na resposta da API: ${response.code()}")
                    val errorBody = response.errorBody()?.string()
                    Log.e("home", "Error body: $errorBody")
                    Toast.makeText(
                        this@NotificacoesUser,
                        "Erro ao buscar queimadas pendentes",
                        Toast.LENGTH_SHORT
                    ).show()
                    updatePendingQueimadasUI(emptyList(), roleType)
                }
            }

            override fun onFailure(call: Call<List<Queimadas>>, t: Throwable) {
                Toast.makeText(
                    this@NotificacoesUser,
                    "Falha na solicitação: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
                updatePendingQueimadasUI(emptyList(), roleType)
            }
        })
    }

    private fun updatePendingQueimadasUI(queimadas: List<Queimadas>, roleType: String) {
        val adapter = QueimadasAdapter2(queimadas, roleType)
        recyclerView.adapter = adapter
    }

    private fun logout() {
        val intent = Intent(this, Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun getUserType(callback: (String?) -> Unit) {
        val idUser = MyApp.userId.toLong()

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
                            service.getRole("eq.$idRoleString")
                                .enqueue(object : Callback<List<Roles>> {
                                    override fun onResponse(
                                        call: Call<List<Roles>>,
                                        response: Response<List<Roles>>
                                    ) {
                                        if (response.isSuccessful) {
                                            val rolesList = response.body()
                                            if (rolesList != null && rolesList.isNotEmpty()) {
                                                val role = rolesList[0]
                                                val roleType = role.type
                                                Log.d("Role", "Tipo de role: $roleType")
                                                callback(roleType)
                                            } else {
                                                Toast.makeText(
                                                    this@NotificacoesUser,
                                                    "Tipo de role não encontrado",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                callback(null)
                                            }
                                        } else {
                                            Toast.makeText(
                                                this@NotificacoesUser,
                                                "Erro ao obter o tipo de role",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            Log.e(
                                                "teste",
                                                "Falha ao obter tipo do role do user: ${response.code()} - ${
                                                    response.errorBody()?.string()
                                                }"
                                            )
                                            callback(null)
                                        }
                                    }

                                    override fun onFailure(call: Call<List<Roles>>, t: Throwable) {
                                        val errorMessage = t.message ?: "Erro desconhecido"
                                        Log.d(
                                            "Erro",
                                            "Falha ao obter o tipo de role: $errorMessage"
                                        )
                                        Toast.makeText(
                                            this@NotificacoesUser,
                                            "Erro ao obter o tipo de role: $errorMessage",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        callback(null)
                                    }
                                })
                        } else {
                            Toast.makeText(
                                this@NotificacoesUser,
                                "ID do role do user não encontrado",
                                Toast.LENGTH_SHORT
                            ).show()
                            callback(null)
                        }
                    } else {
                        Toast.makeText(
                            this@NotificacoesUser,
                            "user não encontrado",
                            Toast.LENGTH_SHORT
                        ).show()
                        callback(null)
                    }
                } else {
                    Toast.makeText(
                        this@NotificacoesUser,
                        "Erro ao obter o ID do role do user",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e(
                        "teste",
                        "Falha ao obter id do role do user: ${response.code()} - ${
                            response.errorBody()?.string()
                        }"
                    )
                    callback(null)
                }
            }

            override fun onFailure(call: Call<List<Users>>, t: Throwable) {
                val errorMessage = t.message ?: "Erro desconhecido"
                Log.d("Erro", "Falha ao obter o ID do role do user: $errorMessage")
                Toast.makeText(
                    this@NotificacoesUser,
                    "Erro ao obter o ID do role do user: $errorMessage",
                    Toast.LENGTH_SHORT
                ).show()
                callback(null)
            }
        })
    }

    private fun getUser(idUser: String, callback: (Users) -> Unit) {
        service.getUserById("eq.${MyApp.userId}").enqueue(object : Callback<List<Users>> {
            override fun onResponse(call: Call<List<Users>>, response: Response<List<Users>>) {
                if (response.isSuccessful) {
                    val usersList = response.body()
                    if (usersList != null && usersList.isNotEmpty()) {
                        val user = usersList[0]
                        Log.d("getUser", "user encontrado: ${user.toString()}")
                        callback(user)
                    } else {
                        Log.d("getUser", "user não encontrado")
                    }
                } else {
                    Log.e("getUser", "Erro na resposta da API: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Users>>, t: Throwable) {
                Log.e("getUser", "Falha na solicitação: ${t.message}", t)
            }
        })
    }
}
