package com.example.chamasegura

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Profile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        // Configurar o clique no botão de avatar
        findViewById<ImageButton>(R.id.avatarButton).setOnClickListener {
            // Aqui você pode abrir uma nova atividade, um diálogo para selecionar uma imagem ou outra ação
            Toast.makeText(this, "Avatar clicked", Toast.LENGTH_SHORT).show()
        }

        // Configurar o clique no botão de voltar
        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            // Aqui você pode finalizar a atividade ou outra ação
            finish()
        }
    }
}