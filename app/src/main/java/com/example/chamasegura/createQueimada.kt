package com.example.chamasegura

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chamasegura.retrofit.RetrofitClient
import com.example.chamasegura.retrofit.SupabaseAuthService
import com.example.chamasegura.retrofit.UpdateQueimadaRequest
import com.example.chamasegura.retrofit.tabels.Queimadas
import com.example.chamasegura.retrofit.tabels.Location
import com.example.chamasegura.retrofit.tabels.TypeQueimadas
import com.example.chamasegura.retrofit.tabels.Aprovation
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class createQueimada : AppCompatActivity() {

    private lateinit var coordenadasEditText: EditText
    private lateinit var tipoSpinner: Spinner
    private lateinit var dataCalendarView: CalendarView
    private lateinit var motivoEditText: EditText
    private lateinit var solicitarAprovacaoButton: Button
    private lateinit var firstName: String
    private var idUser: Long = 0
    private var selectedDate: String = ""
    private lateinit var typeQueimadasList: List<TypeQueimadas>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solicitaraprovacao)

        // Inicialização dos componentes de UI
        coordenadasEditText = findViewById(R.id.coordenadasEditText)
        tipoSpinner = findViewById(R.id.tipoSpinner)
        dataCalendarView = findViewById(R.id.dataEditText)
        motivoEditText = findViewById(R.id.motivoEditText)
        solicitarAprovacaoButton = findViewById(R.id.solicitar_aprovacao_button)

        // Receber o idUser e firstName passados pelo Intent
        idUser = intent.getLongExtra("idUser", 0)
        firstName = intent.getStringExtra("name") ?: "null"

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

        // Configurar CalendarView para pegar a data selecionada
        dataCalendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = java.util.Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val date = calendar.time
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            selectedDate = formatter.format(date)
        }

        // Buscar e popular o Spinner com os tipos de queimadas
        loadTypeQueimadas()
    }

    private fun loadTypeQueimadas() {
        val service = RetrofitClient.instance.create(SupabaseAuthService::class.java)
        service.getTypeQueimadas().enqueue(object : Callback<List<TypeQueimadas>> {
            override fun onResponse(call: Call<List<TypeQueimadas>>, response: Response<List<TypeQueimadas>>) {
                if (response.isSuccessful) {
                    typeQueimadasList = response.body() ?: emptyList()
                    val adapter = ArrayAdapter(
                        this@createQueimada,
                        android.R.layout.simple_spinner_dropdown_item,
                        typeQueimadasList.map { it.type }
                    )
                    tipoSpinner.adapter = adapter
                } else {
                    Toast.makeText(this@createQueimada, "Erro ao buscar tipos de queimadas", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<TypeQueimadas>>, t: Throwable) {
                Toast.makeText(this@createQueimada, "Erro de rede: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun solicitarAprovacao() {
        val coordenadas = coordenadasEditText.text.toString().split(",").map { it.trim() }
        val latitude = coordenadas.getOrNull(0)
        val longitude = coordenadas.getOrNull(1)
        val selectedPosition = tipoSpinner.selectedItemPosition
        val type = typeQueimadasList.getOrNull(selectedPosition)?.idTypeQueimadas
        val data = selectedDate
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
                            adicionarQueimada(locationId, type ?: 0, data, motivo, status, idUser)
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
                    handleErrorResponse(response)
                }
            }

            override fun onFailure(call: Call<List<Location>>, t: Throwable) {
                handleFailure(t)
            }
        })
    }

    private fun handleErrorResponse(response: Response<*>) {
        val errorMessage = "Erro na solicitação: ${response.code()} - ${response.errorBody()?.string()}"
        Log.e("createQueimada", errorMessage)
        Toast.makeText(this@createQueimada, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun handleFailure(t: Throwable) {
        val errorMessage = "Falha na solicitação: ${t.message}"
        Log.e("createQueimada", errorMessage, t)
        Toast.makeText(this@createQueimada, errorMessage, Toast.LENGTH_SHORT).show()
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

    private fun adicionarQueimada(locationId: Long, idTypeQueimadas: Long, data: String, motivo: String, status: String, idUser: Long) {
        val queimadas = Queimadas(idQueimada = null, locationId, idTypeQueimadas, data, motivo, status, idUser, idAprovation = null)

        Log.d("entrou", "entrou")
        val service = RetrofitClient.instance.create(SupabaseAuthService::class.java)
        service.createQueimada(queimadas).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                        Toast.makeText(this@createQueimada, "Solicitação enviada com sucesso", Toast.LENGTH_SHORT).show()
                        ultimoId(queimadas)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Erro desconhecido"
                    Log.d("createQueimada", "Código de resposta: ${response.code()}, Corpo de erro: $errorBody")
                    Toast.makeText(this@createQueimada, "Erro ao enviar solicitação", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@createQueimada, "Falha na solicitação: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("createQueimada", "Falha na solicitação", t)
                Log.e("createQueimada", "Mensagem de erro: ${t.localizedMessage}")
            }
        })
    }

    private fun criarNovaAprovacao(queimada: Queimadas) {
        val aprovacao = Aprovation(idAprovation = null, bombeiros = "Pendente", protecao_civil = "Pendente", municipio = "Pendente")

        Log.d("entrou", "entrou")

        val service = RetrofitClient.instance.create(SupabaseAuthService::class.java)
        service.createAprovation(aprovacao).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("teste", "teste")
                    getUltimoIdAprovation(queimada, aprovacao)
                } else {
                    Log.d(
                        "createQueimada",
                        "Código de resposta: ${response.code()}, Corpo de erro: ${
                            response.errorBody()?.string()
                        }"
                    )
                    Toast.makeText(
                        this@createQueimada,
                        "Erro ao criar aprovação",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(
                    this@createQueimada,
                    "Falha na criação da aprovação: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("createQueimada", "Falha na criação da aprovação", t)
            }
        })
    }

    private fun atualizarQueimadaComIdAprovation(queimada: Queimadas, aprovation: Aprovation) {
        Log.d("entrou", "entrou")

        val idQueimada = queimada.idQueimada ?: run {
            Log.e("createQueimada", "ID de Queimada nulo")
            return
        }

        val idAprovation = aprovation.idAprovation ?: run {
            Log.e("createQueimada", "ID de Aprovação nulo")
            return
        }

        val updateQueimadaRequest = UpdateQueimadaRequest(aprovation.idAprovation)

        Log.d("createQueimada", "ID de Queimada: $idQueimada")
        Log.d("createQueimada", "ID de Aprovation: $idAprovation")

        val service = RetrofitClient.instance.create(SupabaseAuthService::class.java)
        service.updateQueimada("eq.$idQueimada", updateQueimadaRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@createQueimada, "Queimada atualizada com ID de aprovação", Toast.LENGTH_SHORT).show()
                    finish()  // Retorna ao ecrã principal
                } else {
                    Log.d("createQueimada", "Código de resposta: ${response.code()}, Corpo de erro: ${response.errorBody()?.string()}")
                    Toast.makeText(this@createQueimada, "Erro ao atualizar queimada com ID de aprovação", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@createQueimada, "Falha na atualização da queimada: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("createQueimada", "Falha na atualização da queimada", t)
            }
        })
    }


    private fun ultimoId(queimada: Queimadas) {
        val service = RetrofitClient.instance.create(SupabaseAuthService::class.java)
        Log.d("teste2", "teste2")
        // Faz a chamada para obter todas as queimadas
        service.getAllQueimadas().enqueue(object : Callback<List<Queimadas>> {
            override fun onResponse(call: Call<List<Queimadas>>, response: Response<List<Queimadas>>) {
                if (response.isSuccessful) {
                    val queimadasList = response.body()
                    if (queimadasList != null && queimadasList.isNotEmpty()) {
                        // Encontra o maior ID entre todas as queimadas retornadas
                        val ultimoId = queimadasList.maxByOrNull { it.idQueimada ?: 0 }?.idQueimada ?: 0

                        val queimadaCriada = queimada.copy(idQueimada = ultimoId)

                        criarNovaAprovacao(queimadaCriada)
                    } else {
                        println("Nenhuma queimada encontrada")
                    }
                } else {
                    println("Falha ao obter queimadas: ${response.errorBody()?.string() ?: "Erro desconhecido"}")
                }
            }

            override fun onFailure(call: Call<List<Queimadas>>, t: Throwable) {
                println("Falha na solicitação: ${t.message}")
            }
        })
    }

    private fun getUltimoIdAprovation(queimada: Queimadas, aprovation: Aprovation) {
        val service = RetrofitClient.instance.create(SupabaseAuthService::class.java)
        // Faz a chamada para obter todas as aprovations
        service.getAllAprovations().enqueue(object : Callback<List<Aprovation>> {
            override fun onResponse(call: Call<List<Aprovation>>, response: Response<List<Aprovation>>) {
                if (response.isSuccessful) {
                    val aprovationsList = response.body()
                    if (aprovationsList != null && aprovationsList.isNotEmpty()) {
                        // Encontra o maior ID entre todas as aprovations retornadas
                        val ultimoId = aprovationsList.maxByOrNull { it.idAprovation ?: 0 }?.idAprovation ?: 0
                        val aprovationCriada = aprovation.copy(idAprovation = ultimoId)
                        atualizarQueimadaComIdAprovation(queimada, aprovationCriada)
                    } else {
                        println("Nenhuma aprovation encontrada")
                    }
                } else {
                    println("Falha ao obter aprovation: ${response.errorBody()?.string() ?: "Erro desconhecido"}")
                }
            }

            override fun onFailure(call: Call<List<Aprovation>>, t: Throwable) {
                println("Falha na solicitação: ${t.message}")
            }
        })
}


}
