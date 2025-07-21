package com.example.textclientapp

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.textclientapp.appUI.MainScreen
import com.google.gson.Gson
import com.microsoft.agents.client.android.AgentsClientSDK
import com.microsoft.agents.client.android.exceptions.SDKError
import com.microsoft.agents.client.android.models.AppSettings
import com.microsoft.agents.client.android.sdks.ClientSDK

class MainActivity : AppCompatActivity() {

    private var isVoiceRecording by mutableStateOf(false)
    private var agentsClientSdk by mutableStateOf<ClientSDK?>(null)
    private var isSdkInitialized = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val appSettings = loadAppSettings(this)

        setContent {
            initializeSdk(appSettings)
            MainScreen(
                isVoiceRecording = isVoiceRecording,
                onRecordClick = { handleRecordClick() },
                agentsClientSdk = agentsClientSdk
            )
        }

        requestPermissions.launch(
            arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.INTERNET)
        )
    }

    private fun loadAppSettings(context: Context): AppSettings {
        val inputStream = context.resources.openRawResource(R.raw.appsettings)
        val json = inputStream.bufferedReader().use { it.readText() }
        return Gson().fromJson(json, AppSettings::class.java)
    }

    private fun initializeSdk(appSettings: AppSettings) {
        if (!isSdkInitialized) {
            try {
                agentsClientSdk = AgentsClientSDK.initSDK(this@MainActivity, appSettings)
                isSdkInitialized = true
            } catch (e: SDKError) {
                val errorMessage = "${e.code}: ${e.message}"
                Log.e("MainActivity", errorMessage, e)
                showToast(errorMessage)
            }
        }
    }

    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[Manifest.permission.RECORD_AUDIO] == true &&
                permissions[Manifest.permission.INTERNET] == true
            ) {
                Log.e("Permissions", "Permissions granted")
            } else {
                Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
            }
        }

    private fun handleRecordClick() {
        if (isVoiceRecording) {
            agentsClientSdk?.stopContinuousListening()
        } else {
            agentsClientSdk?.startContinuousListening()
        }
        isVoiceRecording = !isVoiceRecording
        agentsClientSdk?.stopSpeaking() // Stop the bot's speech when the user starts speaking
    }

    fun showToast(msg: String) {
        runOnUiThread {
            AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(msg)
                .setPositiveButton("Dismiss") { _, _ ->
                    finishAffinity()
                }
                .setCancelable(false)
                .show()
        }
    }
}