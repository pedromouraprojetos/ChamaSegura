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
import com.example.chamasegura.EditUserActivity
import com.google.android.material.snackbar.Snackbar

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
        userList = users
        notifyDataSetChanged()
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userName: TextView = itemView.findViewById(R.id.userName)
        private val userEmail: TextView = itemView.findViewById(R.id.userEmail)
        private val userAccountState: TextView = itemView.findViewById(R.id.userAccountState)
        private val editIcon: ImageView = itemView.findViewById(R.id.icon1)
        private val deleteIcon: ImageView = itemView.findViewById(R.id.icon2)

        fun bind(user: Users) {
            userName.text = user.name ?: "Nome não fornecido"
            userEmail.text = user.email
            userAccountState.text = user.estado_conta

            editIcon.setOnClickListener { editUser(user) }
            deleteIcon.setOnClickListener { toggleUserAccountState(user) }
        }

        private fun editUser(user: Users) {
            val intent = Intent(itemView.context, EditUserActivity::class.java).apply {
                putExtra("userId", user.idUsers)
                putExtra("userName", user.name)
                putExtra("userEmail", user.email)
                putExtra("userRole", user.idRole)
            }
            itemView.context.startActivity(intent)
        }

        private fun toggleUserAccountState(user: Users) {
            val service = RetrofitClient.instance.create(SupabaseAuthService::class.java)
            val novoEstado = if (user.estado_conta == "Ativo") "Desativado" else "Ativo"
            val updateEstadoConta = UpdateEstadoConta(novoEstado)
            val idUsers = user.idUsers

            service.UpdateEstadoConta("eq.$idUsers", updateEstadoConta).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        user.estado_conta = novoEstado
                        userAccountState.text = novoEstado
                        Snackbar.make(itemView, "Conta $novoEstado com sucesso", Snackbar.LENGTH_SHORT).show()
                        notifyItemChanged(adapterPosition)
                    } else {
                        Log.d("updateStatus", "Código de resposta: ${response.code()}, Corpo de erro: ${response.errorBody()?.string()}")
                        Snackbar.make(itemView, "Erro ao $novoEstado a conta", Snackbar.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("updateStatus", "Falha na atualização do status", t)
                    Snackbar.make(itemView, "Falha ao $novoEstado a conta", Snackbar.LENGTH_SHORT).show()
                }
            })
        }
    }
}
