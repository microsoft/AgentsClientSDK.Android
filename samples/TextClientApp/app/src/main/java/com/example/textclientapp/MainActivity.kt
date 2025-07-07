package com.example.textclientapp

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.textclientapp.appUI.MainScreen
import com.example.textclientapp.ui.theme.TextClientAppTheme
import com.google.gson.Gson
import com.microsoft.agentsclientsdk.AgentsClientSDK
import com.microsoft.agentsclientsdk.models.AppSettings

class MainActivity :
    AppCompatActivity() {

    private var isVoiceRecording by mutableStateOf(false)
    private var isSdkInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appSettings = loadAppSettings(this)
        setContent {
            initializeSdk(appSettings)
            MainScreen(
                isVoiceRecording = isVoiceRecording,
                onRecordClick = { handleRecordClick() }
            )
        }

        requestPermissions.launch(
            arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.INTERNET)
        )
    }

    private fun initializeSdk(appSettings: AppSettings) {
        if (!isSdkInitialized) {
            AgentsClientSDK.init(this@MainActivity, appSettings)
            isSdkInitialized = true
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

    fun handleRecordClick() {
        if (isVoiceRecording) {
            AgentsClientSDK.sdk?.stopContinuousListening()
        } else {
            AgentsClientSDK.sdk?.startContinuousListening()
        }
        isVoiceRecording = !isVoiceRecording
        AgentsClientSDK.sdk?.stopSpeaking() // Stop the bot's speech when the user starts speaking
    }
}

fun loadAppSettings(context: Context): AppSettings {
    val inputStream = context.resources.openRawResource(R.raw.appsettings)
    val json = inputStream.bufferedReader().use { it.readText() }
    return Gson().fromJson(json, AppSettings::class.java)
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TextClientAppTheme {
        Greeting("Android")
    }
}