import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.chamasegura.R
import com.example.chamasegura.retrofit.tabels.Queimadas
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.chamasegura.retrofit.RetrofitClient
import com.example.chamasegura.retrofit.tabels.TypeQueimadas
import android.widget.Toast
import com.example.chamasegura.retrofit.SupabaseAuthService
import com.example.chamasegura.retrofit.tabels.Location

class QueimadasAdapter(private val queimadas: List<Queimadas>) : RecyclerView.Adapter<QueimadasAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val queimada = queimadas[position]
        holder.bind(queimada)
        holder.itemView.findViewById<ImageView>(R.id.arrow_icon).setOnClickListener {
            // Capturar o clique na seta
            // Chamar um método para exibir informações detalhadas da queimada
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
        val dialogView = LayoutInflater.from(context).inflate(R.layout.activity_criarqueimada, null)
        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        val service = RetrofitClient.instance.create(SupabaseAuthService::class.java)

        // Chamada para obter os tipos de queimadas
        service.getTypeQueimadas().enqueue(object : Callback<List<TypeQueimadas>> {
            override fun onResponse(call: Call<List<TypeQueimadas>>, response: Response<List<TypeQueimadas>>) {
                if (response.isSuccessful) {
                    val typeQueimadasList = response.body()
                    // Encontra o tipo correto com base no idTypeQueimadas
                    val typeQueimada = typeQueimadasList?.find { it.idTypeQueimadas == queimada.idTypeQueimadas }

                    if (typeQueimada != null) {
                        dialogView.findViewById<TextView>(R.id.tipoTextView).text = typeQueimada.type
                    } else {
                        dialogView.findViewById<TextView>(R.id.tipoTextView).text = "Tipo não encontrado"
                    }

                    // Continuar com a chamada para obter as localizações
                    service.getLocations().enqueue(object : Callback<List<Location>> {
                        override fun onResponse(call: Call<List<Location>>, response: Response<List<Location>>) {
                            if (response.isSuccessful) {
                                val locationsList = response.body()
                                // Supondo que a queimada tenha uma referência à localização
                                // Aqui você deve encontrar a localização correta com base na queimada
                                val location = locationsList?.find { it.idLocation == queimada.location }

                                if (location != null) {
                                    dialogView.findViewById<TextView>(R.id.coordenadasTextView).text = "(${location.latitude}, ${location.longitude})"
                                } else {
                                    dialogView.findViewById<TextView>(R.id.coordenadasTextView).text = "Localização não encontrada"
                                }

                                dialogView.findViewById<TextView>(R.id.dataTextView).text = queimada.date
                                dialogView.findViewById<TextView>(R.id.MotivoTextView).text = queimada.reason

                                dialog.show()
                            } else {
                                // Tratar erro na resposta da API de localizações
                                Toast.makeText(context, "Erro ao obter as localizações", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<List<Location>>, t: Throwable) {
                            // Tratar falha na requisição de localizações
                            Toast.makeText(context, "Falha na requisição de localizações", Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    // Tratar erro na resposta da API de tipos de queimadas
                    Toast.makeText(context, "Erro ao obter os tipos de queimadas", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<TypeQueimadas>>, t: Throwable) {
                // Tratar falha na requisição de tipos de queimadas
                Toast.makeText(context, "Falha na requisição de tipos de queimadas", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
