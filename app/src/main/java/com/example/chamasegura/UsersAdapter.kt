import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.chamasegura.R
import com.example.chamasegura.retrofit.RetrofitClient
import com.example.chamasegura.retrofit.SupabaseAuthService
import com.example.chamasegura.retrofit.UpdateEstadoConta
import com.example.chamasegura.retrofit.tabels.Users
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.example.chamasegura.EditUserActivity


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
                // Criar um Intent para abrir a atividade de edição do usuário
                val intent = Intent(itemView.context, EditUserActivity::class.java)

                // Passar os dados do usuário para a atividade de edição via Intent
                intent.putExtra("userId", user.idUsers)
                Log.d("teste2", "idUser: ${user.idUsers}")
                intent.putExtra("userName", user.name)
                intent.putExtra("userEmail", user.email)

                // Iniciar a atividade de edição
                itemView.context.startActivity(intent)
            }


            deleteIcon.setOnClickListener {
                val service = RetrofitClient.instance.create(SupabaseAuthService::class.java)
                val novoEstado = if (user.estado_conta == "Ativo") "Desativado" else "Ativo"
                val updateEstadoConta = UpdateEstadoConta(novoEstado)
                val idUsers = user.idUsers

                service.UpdateEstadoConta("eq.$idUsers", updateEstadoConta).enqueue(object :
                    Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            // Atualizar o estado do usuário localmente
                            user.estado_conta = novoEstado

                            // Mostrar Toast de sucesso
                            Toast.makeText(
                                itemView.context,
                                "Conta ${novoEstado.toLowerCase()} com sucesso",
                                Toast.LENGTH_SHORT
                            ).show()

                            // Atualizar a UI
                            notifyItemChanged(adapterPosition)
                        } else {
                            Log.d(
                                "updateStatus",
                                "Código de resposta: ${response.code()}, Corpo de erro: ${
                                    response.errorBody()?.string()
                                }"
                            )
                            // Mostrar Toast de erro
                            Toast.makeText(
                                itemView.context,
                                "Erro ao ${novoEstado.toLowerCase()} a conta",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.e("updateStatus", "Falha na atualização do status", t)
                        // Mostrar Toast de falha
                        Toast.makeText(
                            itemView.context,
                            "Falha ao ${novoEstado.toLowerCase()} a conta",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }

        }
    }
}

