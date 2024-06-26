package com.example.chamasegura

import MyApp
import QueimadasAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chamasegura.retrofit.RetrofitClient
import com.example.chamasegura.retrofit.SupabaseAuthService
import com.example.chamasegura.retrofit.tabels.Queimadas
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomePageUser : AppCompatActivity() {
    private lateinit var drawerLayout: androidx.drawerlayout.widget.DrawerLayout
    private lateinit var recyclerView: RecyclerView
    private var idUser: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_page_user)

        drawerLayout = findViewById(R.id.drawer_layout)
        recyclerView = findViewById(R.id.recycler_view_queimadas)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val fabButton = findViewById<ImageView>(R.id.fab)
        val userRole = MyApp.role


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.navigation_home)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val fullname = "${MyApp.firstName}"
        val idUser = MyApp.userId.toLong()

        if (idUser == 0L) {
            Toast.makeText(this, "Erro: ID do user inválido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val greetingMessage = findViewById<TextView>(R.id.greeting_message)
        greetingMessage.text = "Hello $fullname!"

        findViewById<ImageView>(R.id.notification_icon).setOnClickListener {
            val intent = Intent(this, NotificacoesUser::class.java)
            intent.putExtra("firstName", fullname)
            intent.putExtra("idUser", idUser)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.menu_icon).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        findViewById<ImageView>(R.id.fire_icon).setOnClickListener {}

        findViewById<ImageView>(R.id.profile_icon).setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            intent.putExtra("firstName", fullname)
            intent.putExtra("idUser", idUser)
            startActivity(intent)
        }

        if (userRole.equals("5")) {
            // Torna o botão visível
            fabButton.visibility = View.VISIBLE
            fabButton.setOnClickListener {
                val intent = Intent(this, createQueimada::class.java)
                intent.putExtra("firstName", fullname)
                intent.putExtra("idUser", idUser)
                intent.putExtra("role", userRole)
                startActivityForResult(intent, REQUEST_CODE_CREATE_QUEIMADA)
            }
        } else {
            // Torna o botão invisível
            fabButton.visibility = View.GONE
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

        fetchPendingQueimadas(idUser)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CREATE_QUEIMADA && resultCode == RESULT_OK) {
            fetchPendingQueimadas(idUser)
        }
    }

    companion object {
        const val REQUEST_CODE_CREATE_QUEIMADA = 1
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
        val adapter = QueimadasAdapter(queimadas)
        recyclerView.adapter = adapter
    }

    private fun logout() {
        val intent = Intent(this, Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
