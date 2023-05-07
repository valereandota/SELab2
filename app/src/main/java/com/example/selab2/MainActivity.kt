package com.example.selab2

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Button
import android.view.Menu
import android.view.MenuItem
import android.widget.Switch
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.selab2.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
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
    private lateinit var textListenerButton: Button
    companion object {
        private const val PERMISSION_REQUEST_CODE = 1
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = Firebase.database("https://selab2-fd87c-default-rtdb.europe-west1.firebasedatabase.app")


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        doorSwitch = findViewById(R.id.doorSwitch)
        doorText = findViewById(R.id.doorView)
        lightSwitch = findViewById(R.id.lightsSwitch)
        lightText = findViewById(R.id.lightsView)
        windowSwitch = findViewById(R.id.windowSwitch)
        windowText = findViewById(R.id.windowView)
        textListenerButton = findViewById(R.id.textListener)
        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        val doorReference = database.getReference("door")
        val lightReference = database.getReference("light")
        val windowReference = database.getReference("window")

        val doorListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val door = dataSnapshot.value
                doorSwitch.isChecked = door == "open"
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("loadDoor:onCancelled", databaseError.toException())
            }
        }
        val lightListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val light = dataSnapshot.value
                lightSwitch.isChecked = light == "on"

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("loadLight:onCancelled", databaseError.toException())
            }
        }
        val windowListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val window = dataSnapshot.value
                windowSwitch.isChecked = window == "open"
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("loadWindow:onCancelled", databaseError.toException())
            }
        }
        doorReference.addValueEventListener(doorListener)
        lightReference.addValueEventListener(lightListener)
        windowReference.addValueEventListener(windowListener)
        doorSwitch.setOnClickListener{
            if(doorSwitch.isChecked) {
                doorReference.setValue("closed")
            } else if(!doorSwitch.isChecked) {
                doorReference.setValue("open")
            }
        }
        lightSwitch.setOnClickListener{
            if(lightSwitch.isChecked) {
                lightReference.setValue("off")
            } else if(!lightSwitch.isChecked) {
                lightReference.setValue("on")
            }
        }
        windowSwitch.setOnClickListener{
            if(windowSwitch.isChecked) {
                windowReference.setValue("closed")
            } else if(!windowSwitch.isChecked) {
                windowReference.setValue("open")
            }
        }
        val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        val recognitionListener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {}

            override fun onResults(results: Bundle) {
                val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                Log.d("THISISTHERESULT", matches.toString())
                if (matches != null && matches.contains("light on")) {
                    lightReference.setValue("on")
                }else if (matches != null && matches.contains("open the door")) {
                    doorReference.setValue("open")
                }else if (matches != null && matches.contains("open window")) {
                    windowReference.setValue("open")
                }else if (matches != null && matches.contains("close window")) {
                    windowReference.setValue("closed")
                }else if (matches != null && matches.contains("close door")) {
                    doorReference.setValue("closed")
                }else if (matches != null && matches.contains("light off")) {
                    lightReference.setValue("off")
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
        speechRecognizer.setRecognitionListener(recognitionListener)
        textListenerButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), PERMISSION_REQUEST_CODE)
            } else {
                val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(
                        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                    )
                    putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this@MainActivity.packageName)
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                }
                speechRecognizer.startListening(recognizerIntent)
            }}
        }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
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