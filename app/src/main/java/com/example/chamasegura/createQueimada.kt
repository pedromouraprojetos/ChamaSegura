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
    private var idUser: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solicitaraprovacao)

        // Inicialização dos componentes de UI
        coordenadasEditText = findViewById(R.id.coordenadasEditText)
        tipoEditText = findViewById(R.id.tipoEditText)
        dataEditText = findViewById(R.id.dataEditText)
        motivoEditText = findViewById(R.id.motivoEditText)
        solicitarAprovacaoButton = findViewById(R.id.solicitar_aprovacao_button)

        // Receber o idUser e firstName passados pelo Intent
        idUser = intent.getLongExtra("idUser", 0)
        firstName = intent.getStringExtra("firstName") ?: "null"

        if (idUser == 0L) {
            Toast.makeText(this, "Erro: ID do usuário inválido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Configuração do clique do botão
        solicitarAprovacaoButton.setOnClickListener {
            solicitarAprovacao()
        }

        // Adicionar clique na seta para voltar para a HomePageUser
        findViewById<ImageView>(R.id.menu_icon).setOnClickListener {
            val intent = Intent(this, HomePageUser::class.java)
            intent.putExtra("firstName", firstName)
            intent.putExtra("idUser", idUser) // Certifique-se de passar idUser ao retornar
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
        val status = "Pendente"

        if (latitude == null || longitude == null) {
            Toast.makeText(this@createQueimada, "Coordenadas inválidas", Toast.LENGTH_SHORT).show()
            return
        }

        val service = RetrofitClient.instance.create(SupabaseAuthService::class.java)

        val latitudeQuery = "eq.$latitude"
        val longitudeQuery = "eq.$longitude"

        service.getLocationByCoordinates(latitudeQuery, longitudeQuery).enqueue(object : Callback<List<Location>> {
            override fun onResponse(call: Call<List<Location>>, response: Response<List<Location>>) {
                if (response.isSuccessful) {
                    val locations = response.body()
                    Log.d("createQueimada", "Resposta bem-sucedida: $locations")

                    if (locations.isNullOrEmpty()) {
                        adicionarNovaLocalizacao(latitude, longitude) {
                            solicitarAprovacao()
                        }
                    } else {
                        val locationId = locations.firstOrNull()?.idLocation?.toLong()
                        Log.d("createQueimada3", "Resposta bem-sucedida: $locationId")

                        if (locationId != null) {
                            adicionarQueimada(locationId, tipo, data, motivo, status, idUser)
                            val resultIntent = Intent()
                            resultIntent.putExtra("queimadaDate", data)
                            resultIntent.putExtra("queimadaStatus", status)
                            setResult(RESULT_OK, resultIntent)
                            finish()
                        } else {
                            Toast.makeText(this@createQueimada, "ID de localização inválido", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this@createQueimada, "Falha na solicitação", Toast.LENGTH_SHORT).show()
                    Log.e("createQueimada", "Falha na verificação da localização por coordenadas: ${response.code()} - ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<Location>>, t: Throwable) {
                Toast.makeText(this@createQueimada, "Falha na solicitação: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("createQueimada", "Falha na verificação da localização por coordenadas", t)
            }
        })
    }

    private fun adicionarNovaLocalizacao(latitude: String, longitude: String, callback: () -> Unit) {
        val novaLocalizacao = Location(idLocation = null, latitude = latitude, longitude = longitude)

        val service = RetrofitClient.instance.create(SupabaseAuthService::class.java)
        service.createLocation(novaLocalizacao).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.e("createQueimada", "Teste1: $novaLocalizacao")
                if (response.isSuccessful) {
                    Log.e("createQueimada", "Localização adicionada com sucesso")
                    callback()
                } else {
                    Toast.makeText(this@createQueimada, "Erro ao adicionar nova localização", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                val errorMessage = "Falha ao adicionar nova localização: ${t.message}\n${t.localizedMessage}"
                Toast.makeText(this@createQueimada, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("createQueimada", errorMessage, t)
            }
        })
    }

    private fun adicionarQueimada(locationId: Long, tipo: String, data: String, motivo: String, status: String, idUser: Long) {
        val queimadas = Queimadas(locationId, tipo, data, motivo, status, idUser)

        val service = RetrofitClient.instance.create(SupabaseAuthService::class.java)
        service.createQueimada(queimadas).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@createQueimada, "Solicitação enviada com sucesso", Toast.LENGTH_SHORT).show()
                    finish()  // Retorna ao ecrã principal
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

