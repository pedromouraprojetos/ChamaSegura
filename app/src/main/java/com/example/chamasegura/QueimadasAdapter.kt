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
        val dialogView = LayoutInflater.from(context).inflate(R.layout.activity_queimada_details, null)

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        dialogView.findViewById<TextView>(R.id.text_coordinates).text = "Coordenadas: (${queimada.location})"
        dialogView.findViewById<TextView>(R.id.text_type).text = "Tipo: ${queimada.idTypeQueimadas}"
        dialogView.findViewById<TextView>(R.id.text_date).text = "Data: ${queimada.date}"
        dialogView.findViewById<TextView>(R.id.text_reason).text = "Motivo: ${queimada.reason}"

        dialog.show()
    }
}
