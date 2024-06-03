package com.example.chamasegura

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class NotificacoesUser : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_notificacoes_user)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.notificacoes)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configurar a navegação
        findViewById<ImageView>(R.id.notification_icon).setOnClickListener {
            // Já está na NotificacoesUser, então não precisa de ação aqui.
        }

        findViewById<ImageView>(R.id.fire_icon).setOnClickListener {
            startActivity(Intent(this, HomePageUser::class.java))
        }

        findViewById<ImageView>(R.id.profile_icon).setOnClickListener {
            startActivity(Intent(this, Profile::class.java))
        }
    }
}
