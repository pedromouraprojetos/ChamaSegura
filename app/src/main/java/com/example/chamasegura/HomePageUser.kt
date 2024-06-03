package com.example.chamasegura

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HomePageUser : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_page_user)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.navigation_home)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configurar a navegação
        findViewById<ImageView>(R.id.notification_icon).setOnClickListener {
            startActivity(Intent(this, NotificacoesUser::class.java))
        }

        findViewById<ImageView>(R.id.fire_icon).setOnClickListener {
            // Já está na HomePageUser, então não precisa de ação aqui.
        }

        findViewById<ImageView>(R.id.profile_icon).setOnClickListener {
            startActivity(Intent(this, Profile::class.java))
        }

        findViewById<ImageView>(R.id.fab).setOnClickListener {
            startActivity(Intent(this, createQueimada::class.java))
        }
    }
}
