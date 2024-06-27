package com.example.chamasegura
import UsersAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chamasegura.retrofit.SupabaseAuthService
import com.example.chamasegura.retrofit.RetrofitClient
import com.example.chamasegura.retrofit.tabels.Users
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomePageAdmin : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var usersAdapter: UsersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_page_admin)

        // Initialize DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout)

        // Set up the Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // Remove the title from the toolbar

        // Set up the left icon click listener to open the navigation drawer
        findViewById<ImageView>(R.id.icon_left).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Set up the NavigationView
        val navigationView: NavigationView = findViewById(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_create_user -> {
                    val intent = Intent(this, CreateUserActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_create_rules -> {
                    val intent = Intent(this, CreateRules::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_logout -> {
                    val intent = Intent(this, Login::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewUsers)
        recyclerView.layoutManager = LinearLayoutManager(this)
        usersAdapter = UsersAdapter(emptyList()) // Initially empty list
        recyclerView.adapter = usersAdapter

        // Fetch users from API and update RecyclerView
        fetchUsers()
    }

    private fun fetchUsers() {
        val service = RetrofitClient.instance.create(SupabaseAuthService::class.java)
        val call = service.getAllUsers()

        call.enqueue(object : Callback<List<Users>> {
            override fun onResponse(call: Call<List<Users>>, response: Response<List<Users>>) {
                if (response.isSuccessful) {
                    val users = response.body() ?: emptyList()

                    usersAdapter.updateUsers(users)
                } else {
                    // Handle unsuccessful response
                }
            }

            override fun onFailure(call: Call<List<Users>>, t: Throwable) {
                // Handle failure
            }
        })
    }
}
