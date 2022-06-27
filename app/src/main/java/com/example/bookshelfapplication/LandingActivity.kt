package com.example.bookshelfapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LandingActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)
        drawerLayout = findViewById(R.id.drawer_layout)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(navController.graph,drawerLayout)
        setupActionBarWithNavController(navController,appBarConfiguration)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener {
            drawerLayout.closeDrawers()
            when(it.itemId){
                R.id.my_posts -> navController.navigate(HomeFragmentDirections.actionHomeFragmentToMyPostsFragment())
                R.id.my_requests -> navController.navigate(HomeFragmentDirections.actionHomeFragmentToMyRequestsFragment())
                R.id.sign_out -> {
                    auth.signOut()
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this,"Logged Out Successfully",Toast.LENGTH_LONG).show()
                    finish()
                }
            }
            true
        }

        navController.addOnDestinationChangedListener{_, destination, _ ->
            if(destination.id == R.layout.fragment_home){
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            }else{
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }

        auth = Firebase.auth
        setUpDrawerHeader(auth.currentUser,navigationView)
    }

    private fun setUpDrawerHeader(user: FirebaseUser?, navigationView: NavigationView){
        val header: View = navigationView.getHeaderView(0)
        val name: TextView = header.findViewById(R.id.name)
        val email: TextView = header.findViewById(R.id.email)
        val displayPicture: ImageView = header.findViewById(R.id.displayPicture)
        name.text = "${user?.displayName}"
        email.text = "${user?.email}"
        Glide.with(this).load(user?.photoUrl).circleCrop().into(displayPicture)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}