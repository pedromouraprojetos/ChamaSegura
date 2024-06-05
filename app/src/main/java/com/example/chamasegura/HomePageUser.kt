package com.example.chamasegura

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.navigation.NavigationView
import android.widget.Toast

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
        greetingMessage.text = "Boas $firstName,"

        // Configurar a navegação
        findViewById<ImageView>(R.id.notification_icon).setOnClickListener {
            val intent = Intent(this, NotificacoesUser::class.java)
            intent.putExtra("firstName", firstName)
            intent.putExtra("idUser", idUser)
            startActivity(intent)
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
    }

    private fun logout() {
        // Clear user session and navigate to login screen
        val intent = Intent(this, Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
