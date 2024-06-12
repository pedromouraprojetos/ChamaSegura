import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response
import com.example.chamasegura.R
import com.example.chamasegura.retrofit.RetrofitClient
import com.example.chamasegura.retrofit.SupabaseAuthService
import com.example.chamasegura.retrofit.tabels.Queimadas
import com.example.chamasegura.retrofit.UpdateAprovationBombeirosRequest
import com.example.chamasegura.retrofit.UpdateAprovationProtecaoCivilRequest
import com.example.chamasegura.retrofit.UpdateAprovationMunicipioRequest
import com.example.chamasegura.retrofit.UpdateAprovationAdminRequest

class QueimadasAdapter2(private val queimadas: List<Queimadas>, private val roleType: String) : RecyclerView.Adapter<QueimadasAdapter2.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val queimada = queimadas[position]
        holder.bind(queimada)
        holder.itemView.findViewById<ImageView>(R.id.arrow_icon).setOnClickListener {
            showQueimadaDetails(holder.itemView.context, queimada)
        }
    }


    override fun getItemCount(): Int {
        return queimadas.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textDate: TextView = itemView.findViewById(R.id.text_date)
        private val textStatus: TextView = itemView.findViewById(R.id.text_status)

        fun bind(queimada: Queimadas) {
            textDate.text = queimada.date
            textStatus.text = queimada.status
        }
    }

    private fun showQueimadaDetails(context: Context, queimada: Queimadas) {
        var status = "Pendente"
        val dialogView = LayoutInflater.from(context).inflate(R.layout.activity_queimada_details2, null)

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        dialogView.findViewById<TextView>(R.id.text_coordinates).text = "Coordenadas: (${queimada.location})"
        dialogView.findViewById<TextView>(R.id.text_type).text = "Tipo: ${queimada.idTypeQueimadas}"
        dialogView.findViewById<TextView>(R.id.text_date).text = "Data: ${queimada.date}"
        dialogView.findViewById<TextView>(R.id.text_reason).text = "Motivo: ${queimada.reason}"

        dialog.show()

        // Configurar cliques nos botões check e cross
        dialogView.findViewById<ImageView>(R.id.check).setOnClickListener {
            Toast.makeText(dialogView.context, "Queimada Aceite com Sucesso", Toast.LENGTH_SHORT).show()
            status = "Confirmada"
            setStatus(status, queimada.idAprovation)
        }

        dialogView.findViewById<ImageView>(R.id.cross).setOnClickListener {
            Toast.makeText(dialogView.context, "Queimada Recusada com Sucesso", Toast.LENGTH_SHORT).show()
            status = "Recusada"
            setStatus(status, queimada.idAprovation)
        }
    }

    private fun setStatus(status: String, idAprovation: Long?) {
        val service = RetrofitClient.instance.create(SupabaseAuthService::class.java)

        when (roleType) {
            "Admin" -> {
                val service = RetrofitClient.instance.create(SupabaseAuthService::class.java)
                Log.d("status", "status: $status")
                val updateAprovationAdminRequest = UpdateAprovationAdminRequest(status)

                service.updateAprovationAdmin("eq.$idAprovation", updateAprovationAdminRequest).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                        } else {
                            Log.d("updateAdminStatus", "Código de resposta: ${response.code()}, Corpo de erro: ${response.errorBody()?.string()}")
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.e("updateAdminStatus", "Falha na atualização do status", t)
                    }
                })
            }
            "Bombeiros" -> {
                val service = RetrofitClient.instance.create(SupabaseAuthService::class.java)
                Log.d("status", "status: $status")
                val updateAprovationBombeirosRequest = UpdateAprovationBombeirosRequest(status)

                service.updateAprovationBombeiros("eq.$idAprovation", updateAprovationBombeirosRequest).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                        } else {
                            Log.d("updateBombeirosStatus", "Código de resposta: ${response.code()}, Corpo de erro: ${response.errorBody()?.string()}")
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.e("updateBombeirosStatus", "Falha na atualização do status", t)
                    }
                })
            }
            "Proteção Civil" -> {
                val service = RetrofitClient.instance.create(SupabaseAuthService::class.java)
                Log.d("status", "status: $status")
                val updateAprovationProtecaoCivilRequest = UpdateAprovationProtecaoCivilRequest(status)

                service.updateAprovationProtecaoCivil("eq.$idAprovation", updateAprovationProtecaoCivilRequest).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                        } else {
                            Log.d("updateProtecaoCivilStatus", "Código de resposta: ${response.code()}, Corpo de erro: ${response.errorBody()?.string()}")
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.e("updateProtecaoCivilStatus", "Falha na atualização do status", t)
                    }
                })
            }
            "Municipio" -> {
                val service = RetrofitClient.instance.create(SupabaseAuthService::class.java)
                Log.d("status", "status: $status")
                val updateAprovationMunicipioRequest = UpdateAprovationMunicipioRequest(status)

                service.updateAprovationMunicipio("eq.$idAprovation", updateAprovationMunicipioRequest).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                        } else {
                            Log.d("updateMunicipioStatus", "Código de resposta: ${response.code()}, Corpo de erro: ${response.errorBody()?.string()}")
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.e("updateMunicipioStatus", "Falha na atualização do status", t)
                    }
                })
            }
        }
    }
}
