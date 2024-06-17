import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chamasegura.R
import com.example.chamasegura.retrofit.tabels.Users

class UsersAdapter(private var userList: List<Users>) :
    RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_user_card, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun updateUsers(users: List<Users>) {
        userList = users // Atualiza a lista de usuários
        notifyDataSetChanged() // Notifica o RecyclerView que os dados mudaram
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userName: TextView = itemView.findViewById(R.id.userName)
        private val userEmail: TextView = itemView.findViewById(R.id.userEmail)
        private val editIcon: ImageView = itemView.findViewById(R.id.icon1)
        private val deleteIcon: ImageView = itemView.findViewById(R.id.icon2)

        fun bind(user: Users) {
            userName.text = user.name ?: "Nome não fornecido"
            userEmail.text = user.email

            // Definir listeners para os ícones, se necessário
            editIcon.setOnClickListener {
                // Lógica para editar o usuário
            }

            deleteIcon.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val user = userList[position]

                    // Aqui você pode implementar a lógica para confirmar a exclusão do usuário
                    // Por exemplo, exibir um diálogo de confirmação antes de excluir o usuário

                    // Exemplo: Exibir um AlertDialog para confirmar a exclusão
                    val context = itemView.context
                    AlertDialog.Builder(context)
                        .setTitle("Confirmar Exclusão")
                        .setMessage("Tem certeza que deseja excluir ${user.name}?")
                        .setPositiveButton("Excluir") { _, _ ->
                            // Lógica para excluir o usuário
                            // Implemente aqui a chamada para a API ou a lógica para excluir o usuário

                            // Exemplo: Remover o usuário da lista
                            userList = userList.filterNot { it.idUsers == user.idUsers }
                            notifyDataSetChanged()
                        }
                        .setNegativeButton("Cancelar", null)
                        .show()
                }
            }
        }
    }
}
