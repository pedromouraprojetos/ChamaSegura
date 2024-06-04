package com.example.chamasegura

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chamasegura.retrofit.RetrofitClient
import com.example.chamasegura.retrofit.SupabaseAuthService
import com.example.chamasegura.retrofit.tabels.Queimadas
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class createQueimada : AppCompatActivity() {

    private lateinit var localizacaoEditText: EditText
    private lateinit var coordenadasEditText: EditText
    private lateinit var tipoEditText: EditText
    private lateinit var dataEditText: EditText
    private lateinit var motivoEditText: EditText
    private lateinit var solicitarAprovacaoButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solicitaraprovacao)

        // Inicialização dos componentes de UI
        localizacaoEditText = findViewById(R.id.localizacaoEditText)
        coordenadasEditText = findViewById(R.id.coordenadasEditText)
        tipoEditText = findViewById(R.id.tipoEditText)
        dataEditText = findViewById(R.id.dataEditText)
        motivoEditText = findViewById(R.id.motivoEditText)
        solicitarAprovacaoButton = findViewById(R.id.solicitar_aprovacao_button)

        // Configuração do clique do botão
        solicitarAprovacaoButton.setOnClickListener {
            solicitarAprovacao()
        }
    }

    private fun solicitarAprovacao() {
        // Obtenção dos valores dos campos de texto
        val localizacao = localizacaoEditText.text.toString()
        val coordenadas = coordenadasEditText.text.toString()
        val tipo = tipoEditText.text.toString()
        val data = dataEditText.text.toString()
        val motivo = motivoEditText.text.toString()

        // Criação do objeto Queimadas com os dados inseridos pelo usuário
        val queimadas = Queimadas(localizacao, coordenadas, tipo, data, motivo)

        // Criação do serviço Retrofit
        val service = RetrofitClient.instance.create(SupabaseAuthService::class.java)

        // Chamada para a API para criar a queimada
        service.createQueimada(queimadas).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@createQueimada, "Solicitação enviada com sucesso", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d("createQueimada", "Código de resposta: ${response.code()}")
                    Toast.makeText(this@createQueimada, "Erro ao enviar solicitação", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@createQueimada, "Falha na solicitação: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("createQueimada", "Falha na solicitação", t)
            }
        })
    }
}
