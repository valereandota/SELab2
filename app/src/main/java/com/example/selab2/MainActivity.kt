package com.example.selab2

import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.widget.Switch
import android.widget.TextView
import com.example.selab2.databinding.ActivityMainBinding
import com.google.android.material.slider.Slider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var doorSwitch: Switch
    private lateinit var lightSwitch: Switch
    private lateinit var windowSwitch: Switch
    private lateinit var lightText:TextView
    private lateinit var windowText:TextView
    private lateinit var doorText:TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = Firebase.database("https://selab2-fd87c-default-rtdb.europe-west1.firebasedatabase.app")

        doorSwitch = findViewById(R.id.doorSwitch)
        doorText = findViewById(R.id.doorView)
        lightSwitch = findViewById(R.id.lightsSwitch)
        lightText = findViewById(R.id.lightsView)
        windowSwitch = findViewById(R.id.windowSwitch)
        windowText = findViewById(R.id.windowView)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val post = dataSnapshot.getValue<Post>()
                // ...
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        postReference.addValueEventListener(postListener)

    }

 fun writeDB(value:String, db: String){
     val database = Firebase.database("https://selab2-fd87c-default-rtdb.europe-west1.firebasedatabase.app")
     val myRef = database.getReference(db)
     myRef.setValue(value)
 }
    fun readDB(db : String){
        val database = Firebase.database("https://selab2-fd87c-default-rtdb.europe-west1.firebasedatabase.app")
        val myRef = database.getReference(db)
        myRef.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = snapshot.getValue<String>()
                Log.d("TEST", "Value is: " + value)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TEST", "Failed to read value.", error.toException())
            }

        })
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}