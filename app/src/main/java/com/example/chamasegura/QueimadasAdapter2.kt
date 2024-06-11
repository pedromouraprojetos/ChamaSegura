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
        var status: String = "Pendente"
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
            val idQueimada = queimada.idQueimada
            Toast.makeText(dialogView.context, "Queimada Aceite com Sucesso", Toast.LENGTH_SHORT).show()
            status = "Confirmada"
        }

        dialogView.findViewById<ImageView>(R.id.cross).setOnClickListener {
            val idQueimada = queimada.idQueimada
            Toast.makeText(dialogView.context, "Queimada Recusada com Sucesso", Toast.LENGTH_SHORT).show()
            status = "Recusada"
        }

        setStatus(status)
    }

    private fun setStatus(status: String) {
        val service = RetrofitClient.instance.create(SupabaseAuthService::class.java)

        when (roleType) {
            "Admin" -> {

            }
            "Bombeiros" -> {
                val call = service.addAprovationEntry(status)

                call.enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            // Processamento bem-sucedido
                            Log.d("AddAprovation", "Aprovação adicionada para Bombeiros")
                        } else {
                            // Lidar com erros de resposta
                            Log.e("AddAprovation", "Erro ao adicionar aprovação para Bombeiros: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        // Lidar com falhas na comunicação
                        Log.e("AddAprovation", "Falha na solicitação de adição de aprovação para Bombeiros: ${t.message}")
                    }
                })
            }
            "Proteção Civil" -> {

            }
            "Municipio" -> {

            }
            else -> {

            }
        }
    }
}
