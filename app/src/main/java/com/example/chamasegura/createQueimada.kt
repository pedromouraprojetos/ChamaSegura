package com.example.chamasegura

import MyApp
import MyApp.Companion.userId
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chamasegura.retrofit.RetrofitClient
import com.example.chamasegura.retrofit.SupabaseAuthService
import com.example.chamasegura.retrofit.UpdateInfoUserService
import com.example.chamasegura.retrofit.UpdateQueimadaRequest
import com.example.chamasegura.retrofit.UpdateUserRequest
import com.example.chamasegura.retrofit.tabels.Queimadas
import com.example.chamasegura.retrofit.tabels.Location
import com.example.chamasegura.retrofit.tabels.TypeQueimadas
import com.example.chamasegura.retrofit.tabels.Aprovation
import com.example.chamasegura.retrofit.tabels.Users
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class createQueimada : AppCompatActivity() {

    private lateinit var coordenadasEditText: EditText
    private lateinit var tipoSpinner: Spinner
    private lateinit var dataCalendarView: EditText
    private lateinit var motivoEditText: EditText
    private lateinit var solicitarAprovacaoButton: Button
    private lateinit var firstName: String
    private var idUser: Long = 0
    private var selectedDate: String = ""
    private lateinit var typeQueimadasList: List<TypeQueimadas>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solicitaraprovacao)

        dataCalendarView = findViewById(R.id.datarealizacao)

        dataCalendarView.setOnClickListener { showDatePickerDialog(dataCalendarView) }

        // Inicialização dos componentes de UI
        coordenadasEditText = findViewById(R.id.coordenadasEditText)
        tipoSpinner = findViewById(R.id.tipoSpinner)
        dataCalendarView = findViewById(R.id.datarealizacao)
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
        val data = dataCalendarView.text.toString()
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
                            val idQueimada = 0.toLong()

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

        // Chama a função getMunicipalities para obter o idMunicipalities correspondente ao idUser
        getMunicipalities(idUser,
            // Callback para tratamento geral
            { result ->
                // Aqui dentro, você pode usar o resultado obtido da API para tratamento geral
                if (result != null) {
                    Log.d("MainActivity", "Municipality ID: $result")
                } else {
                    Log.e("MainActivity", "Erro ao obter municípios")
                }
            },
            // Callback para obter o idMunicipalities específico
            { idMunicipalities ->
                idMunicipalities?.let { municipalities ->
                    // Aqui você pode criar o objeto Queimadas com o idMunicipalities obtido
                    val queimadas = Queimadas(
                        idQueimada = null,
                        location = locationId,
                        idTypeQueimadas = idTypeQueimadas,
                        date = data,
                        reason = motivo,
                        status = status,
                        idUser = idUser,
                        idAprovation = null,
                        idMunicipalities = municipalities // Aqui idMunicipalities é do tipo String conforme obtido da API
                    )

                    // Inicia a requisição para criar a queimada usando Retrofit
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
                } ?: run {
                    // Trate o caso em que idMunicipalities é null
                    Log.e("createQueimada", "Não foi possível obter o idMunicipalities para o idUser: $idUser")
                    Toast.makeText(this@createQueimada, "Não foi possível obter o idMunicipalities", Toast.LENGTH_SHORT).show()
                }
            }
        )
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

    private fun showDatePickerDialog(editText: EditText) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->
            val date = "$year-${String.format("%02d", monthOfYear + 1)}-${String.format("%02d", dayOfMonth)}"
            editText.setText(date)
        }, year, month, day)
        datePickerDialog.show()
    }

    private fun getMunicipalities(idUser: Long, callback: (String?) -> Unit, municipalityIdCallback: (String?) -> Unit) {
        val service = RetrofitClient.instance.create(SupabaseAuthService::class.java)

        service.getRoleIdByUserId("eq.$idUser").enqueue(object : Callback<List<Users>> {
            override fun onResponse(call: Call<List<Users>>, response: Response<List<Users>>) {
                if (response.isSuccessful) {
                    val users = response.body()
                    if (users != null && users.isNotEmpty()) {
                        // Supondo que há apenas um usuário com o id específico
                        val municipalityId = users[0].idMunicipalities.toString()
                        Log.d("createQueimada", "Municipality ID: $municipalityId")
                        municipalityIdCallback.invoke(municipalityId)
                    } else {
                        Log.e("createQueimada", "Usuário não encontrado ou lista vazia")
                        municipalityIdCallback.invoke(null) // Usuário não encontrado ou lista vazia
                    }
                } else {
                    Log.e("createQueimada", "Erro na resposta: ${response.code()}")
                    municipalityIdCallback.invoke(null) // Tratar o caso de resposta não bem-sucedida aqui
                }

                // A chamada original do callback pode ser mantida para casos onde você precisa apenas verificar o sucesso
                callback.invoke(null)
            }

            override fun onFailure(call: Call<List<Users>>, t: Throwable) {
                Log.e("createQueimada", "Falha na comunicação: ${t.message}")
                callback.invoke(null) // Tratar falhas na comunicação com a API aqui

                // No caso de falha, também chame o callback do município com null
                municipalityIdCallback.invoke(null)
            }
        })
    }



}
