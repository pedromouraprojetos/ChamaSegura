package com.example.chamasegura

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chamasegura.retrofit.RetrofitClient
import com.example.chamasegura.retrofit.SupabaseAuthService
import com.example.chamasegura.retrofit.SupabaseCreateService
import com.example.chamasegura.retrofit.tabels.Municipalities
import com.example.chamasegura.retrofit.tabels.Rules
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class CreateRules : AppCompatActivity() {

    private lateinit var tipoSpinner: Spinner
    private lateinit var motivoEditText: EditText
    private lateinit var inicioRegraEditText: EditText
    private lateinit var fimRegraEditText: EditText
    private lateinit var saveRulesButton: Button
    private lateinit var typeMunicipalitiesList: List<Municipalities>
    private lateinit var municipalitiesIds: List<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_rules)

        inicioRegraEditText = findViewById(R.id.inicioRegra)
        fimRegraEditText = findViewById(R.id.fimRegra)

        inicioRegraEditText.setOnClickListener { showDatePickerDialog(inicioRegraEditText) }
        fimRegraEditText.setOnClickListener { showDatePickerDialog(fimRegraEditText) }

        // Inicializar os componentes de UI
        tipoSpinner = findViewById(R.id.tipoSpinner)
        motivoEditText = findViewById(R.id.motivoEditText)
        inicioRegraEditText = findViewById(R.id.inicioRegra)
        fimRegraEditText = findViewById(R.id.fimRegra)
        saveRulesButton = findViewById(R.id.saveRules)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configurar o botão "Salvar Regra"
        saveRulesButton.setOnClickListener {
            saveRules()
        }

        loadMunicipalities()
    }

    private fun saveRules() {
        val selectedTypeIndex = tipoSpinner.selectedItemPosition
        val selectedTypeId = municipalitiesIds[selectedTypeIndex]
        val motivo = motivoEditText.text.toString()
        val inicioRegra = inicioRegraEditText.text.toString()
        val fimRegra = fimRegraEditText.text.toString()

        if (motivo.isBlank()) {
            Toast.makeText(this, "Por favor, digite o motivo", Toast.LENGTH_SHORT).show()
            Log.d("CreateRules", "Motivo vazio")
            return
        }
        if (inicioRegra.isBlank()) {
            Toast.makeText(this, "Por favor, selecione a data de início", Toast.LENGTH_SHORT).show()
            Log.d("CreateRules", "Data de início vazia")
            return
        }
        if (fimRegra.isBlank()) {
            Toast.makeText(this, "Por favor, selecione a data de fim", Toast.LENGTH_SHORT).show()
            Log.d("CreateRules", "Data de fim vazia")
            return
        }
        if (inicioRegra > fimRegra) {
            Toast.makeText(this, "A data de início não pode ser maior que a data de fim", Toast.LENGTH_SHORT).show()
            Log.d("CreateRules", "Data de início maior que a data de fim")
            return
        }

        val service = RetrofitClient.instance.create(SupabaseAuthService::class.java)
        val rule = Rules(null, selectedTypeId.toString(), inicioRegra, fimRegra, motivo)

        service.createRules(rule).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@CreateRules, "Registo bem-sucedido", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@CreateRules, Login::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    showError("Erro no registo: $errorBody")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showError("Falha na conexão: ${t.message}")
            }
        })

        // Log the user's input
        Log.d("CreateRules", "Distrito ID: $selectedTypeId, Motivo: $motivo, Início: $inicioRegra, Fim: $fimRegra")
    }

    private fun loadMunicipalities() {
        val service = RetrofitClient.instance.create(SupabaseAuthService::class.java)
        service.getTypeMunicipalities().enqueue(object : Callback<List<Municipalities>> {
            override fun onResponse(
                call: Call<List<Municipalities>>,
                response: Response<List<Municipalities>>
            ) {
                if (response.isSuccessful) {
                    typeMunicipalitiesList = response.body() ?: emptyList()
                    municipalitiesIds = typeMunicipalitiesList.map { it.idMunicipalities }
                    val adapter = ArrayAdapter(
                        this@CreateRules,
                        android.R.layout.simple_spinner_dropdown_item,
                        typeMunicipalitiesList.map { it.name }
                    )
                    tipoSpinner.adapter = adapter
                } else {
                    Toast.makeText(
                        this@CreateRules,
                        "Erro ao buscar tipos de queimadas",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Municipalities>>, t: Throwable) {
                Toast.makeText(this@CreateRules, "Erro de rede: ${t.message}", Toast.LENGTH_SHORT)
                    .show()
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

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        motivoEditText.error = message
        inicioRegraEditText.error = message
        fimRegraEditText.error = message
        Log.e("Rules", message)
    }
}
