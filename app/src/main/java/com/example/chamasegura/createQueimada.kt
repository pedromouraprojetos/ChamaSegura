package com.example.chamasegura

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chamasegura.retrofit.RetrofitClient
import com.example.chamasegura.retrofit.SupabaseAuthService
import com.example.chamasegura.retrofit.tabels.Queimadas
import com.example.chamasegura.retrofit.tabels.Location
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class createQueimada : AppCompatActivity() {

    private lateinit var coordenadasEditText: EditText
    private lateinit var tipoEditText: EditText
    private lateinit var dataEditText: EditText
    private lateinit var motivoEditText: EditText
    private lateinit var solicitarAprovacaoButton: Button
    private lateinit var firstName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solicitaraprovacao)

        // Inicialização dos componentes de UI
        coordenadasEditText = findViewById(R.id.coordenadasEditText)
        tipoEditText = findViewById(R.id.tipoEditText)
        dataEditText = findViewById(R.id.dataEditText)
        motivoEditText = findViewById(R.id.motivoEditText)
        solicitarAprovacaoButton = findViewById(R.id.solicitar_aprovacao_button)

        // Configuração do clique do botão
        solicitarAprovacaoButton.setOnClickListener {
            solicitarAprovacao()
        }

        // Adicionar clique na seta para voltar para a HomePageUser
        findViewById<ImageView>(R.id.menu_icon).setOnClickListener {
            val intent = Intent(this, HomePageUser::class.java)
            intent.putExtra("firstName", firstName)
            startActivity(intent)
            finish()
        }
    }

    private fun solicitarAprovacao() {
        val coordenadas = coordenadasEditText.text.toString().split(",").map { it.trim() }
        val latitude = coordenadas.getOrNull(0)
        val longitude = coordenadas.getOrNull(1)
        val tipo = tipoEditText.text.toString()
        val data = dataEditText.text.toString()
        val motivo = motivoEditText.text.toString()

        if (latitude == null || longitude == null) {
            Toast.makeText(this@createQueimada, "Coordenadas inválidas", Toast.LENGTH_SHORT).show()
            return
        }

        // Criação do serviço Retrofit
        val service = RetrofitClient.instance.create(SupabaseAuthService::class.java)

        // Chamada para verificar se a localização já existe
        val latitudeQuery = "eq.$latitude"
        val longitudeQuery = "eq.$longitude"

        service.getLocationByCoordinates(latitudeQuery, longitudeQuery).enqueue(object : Callback<List<Location>> {
            override fun onResponse(call: Call<List<Location>>, response: Response<List<Location>>) {
                if (response.isSuccessful) {
                    val locations = response.body()
                    if (locations.isNullOrEmpty()) {
                        // Se a localização não existir, cria uma nova e adiciona a queimada
                        adicionarNovaLocalizacaoEQueimada(latitude, longitude, tipo, data, motivo)
                    } else {
                        // Se a localização existir, obtém o ID correspondente e adiciona a queimada
                        val locationId = locations.firstOrNull()?.id?.toInt()
                        if (locationId != null) {
                            adicionarQueimada(locationId, tipo, data, motivo)
                        } else {
                            Toast.makeText(this@createQueimada, "ID de localização inválido", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    // Tratar falha na solicitação
                    Toast.makeText(this@createQueimada, "Falha na solicitação", Toast.LENGTH_SHORT).show()
                    Log.e("createQueimada", "Falha na verificação da localização por coordenadas: ${response.code()} - ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<Location>>, t: Throwable) {
                // Tratar falha na solicitação
                Toast.makeText(this@createQueimada, "Falha na solicitação: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("createQueimada", "Falha na verificação da localização por coordenadas", t)
            }
        })
    }

    private fun adicionarNovaLocalizacaoEQueimada(latitude: String, longitude: String, tipo: String, data: String, motivo: String) {
        val novaLocalizacao = Location(null, latitude, longitude)

        val service = RetrofitClient.instance.create(SupabaseAuthService::class.java)
        service.createLocation(novaLocalizacao).enqueue(object : Callback<Location> {
            override fun onResponse(call: Call<Location>, response: Response<Location>) {
                if (response.isSuccessful) {
                    val location = response.body()
                    if (location != null) {
                        val locationId: Int? = location.id // Alterado para Int?
                        if (locationId != null) {
                            adicionarQueimada(locationId, tipo, data, motivo)
                        } else {
                            Toast.makeText(this@createQueimada, "ID de localização inválido", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@createQueimada, "Localização não retornada", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@createQueimada, "Erro ao adicionar nova localização", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Location>, t: Throwable) {
                Toast.makeText(this@createQueimada, "Falha ao adicionar nova localização: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("createQueimada", "Falha ao adicionar nova localização", t)
            }
        })
    }

    private fun adicionarQueimada(locationId: Int, tipo: String, data: String, motivo: String) {
        val queimadas = Queimadas(locationId, tipo, data, motivo)

        val service = RetrofitClient.instance.create(SupabaseAuthService::class.java)
        service.createQueimada(queimadas).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@createQueimada, "Solicitação enviada com sucesso", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d("createQueimada", "Código de resposta: ${response.code()}, Corpo de erro: ${response.errorBody()?.string()}")
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
