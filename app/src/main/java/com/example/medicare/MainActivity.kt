package com.example.medicare

import android.os.Bundle
import android.util.Log
import android.view.Menu
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.medicare.model.Client
import com.example.medicare.model.User
import com.example.medicare.model.UserClients
import com.example.medicare.network.MedicareAPI
import com.example.medicare.network.NetworkService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private val medicareAPI : MedicareAPI = NetworkService.medicareAPI
    private lateinit var userClients: List<UserClients>
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var user: User
    private var clients: MutableList<Client> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        DB.set_LoggedIn(true)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_home), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        user = intent.getSerializableExtra(KEYS.USER) as User
        initializeUserClientList()
    }

    private fun intializeAllClients() {
        for (userClient in userClients){
            medicareAPI.getClientsById(userClient.client_id).enqueue(object: Callback<Client>{
                override fun onResponse(call: Call<Client>, response: Response<Client>) {
                    if(response.isSuccessful){
                        clients.add(response.body()!!)
                        Log.i("retrofit","onResponse clients: ${clients.toString()}")
                    }
                }

                override fun onFailure(call: Call<Client>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })
        }



    }

    private fun initializeUserClientList() {
        medicareAPI.getUserClientsById(user.id).enqueue(
            object : Callback<List<UserClients>> {
                override fun onResponse(call: Call<List<UserClients>>, response: Response<List<UserClients>>) {
                    Log.i("retrofit", "onResponse: " + response.message())
                    if (response.isSuccessful){
                        userClients = response.body()!!
                        Log.i("retrofit", "onResponse: ${response.body()}")
                        intializeAllClients()
                    }

                }

                override fun onFailure(call: Call<List<UserClients>>, t: Throwable) {
                    Log.i("retrofit", "onFailure: " + t.message)
                }
            }
        )
    }




    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
        DB.sharedPreferences = getPreferences(MODE_PRIVATE)

    }
}