package com.example.medicare

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.medicare.model.Client
import com.example.medicare.model.User
import com.example.medicare.model.UserClients
import com.example.medicare.network.MedicareAPI
import com.example.medicare.network.NetworkService
import com.example.medicare.ui.navigation_drawer.ProtezFragment
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private val medicareAPI: MedicareAPI = NetworkService.medicareAPI
    private lateinit var userClients: List<UserClients>
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var user: User
    private lateinit var navView: NavigationView

    private var clients: MutableList<Client> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        navView.setNavigationItemSelectedListener { it: MenuItem ->
            Log.i("mineMine0", "onCreate: ${it.itemId}")
            getSupportActionBar()?.setTitle(it.title);
            supportFragmentManager.beginTransaction().replace(R.id.frameLayout,ProtezFragment().newInstance(it.itemId)).commit()
            drawerLayout.closeDrawers()
            true
        }

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        //Set autologin
        DB.set_LoggedIn(true)
        //get user info
        user = intent.getSerializableExtra(KEYS.USER) as User
        //set up name and email in nav header
        setNavHeader()

        CoroutineScope(IO).launch {
            getResultsApi()
        }
    }


    private suspend fun getResultsApi() {
        delay(1000)
        initializeUserClientList()
        delay(1000)
        initializeAllClients()
        delay(1000)
        setMenu()
    }


    private fun setNavHeader() {
        val root = navView.getHeaderView(0)
        val userNameTextView = root.findViewById<TextView>(R.id.userName)
        val userEmailTextView = root.findViewById<TextView>(R.id.userEmail)
        userNameTextView.text = user.name
        userEmailTextView.text = user.email
    }

    private fun initializeAllClients() {
        for (userClient in userClients) {
            medicareAPI.getClientsById(userClient.client_id).enqueue(object : Callback<Client> {
                override fun onResponse(call: Call<Client>, response: Response<Client>) {
                    if (response.isSuccessful) {
                        clients.add(response.body()!!)
                        Log.i("retrofit", "onResponse clients: ${clients + ""}")
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
                override fun onResponse(
                    call: Call<List<UserClients>>,
                    response: Response<List<UserClients>>
                ) {
                    Log.i("retrofit", "onResponse: " + response.message())
                    if (response.isSuccessful) {
                        userClients = response.body()!!
                        Log.i("retrofit", "onResponse: ${response.body()}")
                    }

                }

                override fun onFailure(call: Call<List<UserClients>>, t: Throwable) {
                    Log.i("retrofit", "onFailure: " + t.message)
                }
            }
        )
    }

    private suspend fun setMenu() {
        withContext(Dispatchers.Main) {
            val menu = navView.menu

            val submenu = menu.addSubMenu("Users")
            for (client in clients) {
                val id = KEYS.CLIENT_ID_HEADER + client.id
                submenu.add(0, id, 0, "${client.name}  ${client.surname}")
                submenu.setIcon(R.drawable.company_image)
            }

            navView.invalidate()
        }

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.i("mineMine", "onOptionsItemSelected: ${item.itemId}")
        return super.onOptionsItemSelected(item)
    }



}
