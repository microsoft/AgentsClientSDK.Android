# Welcome to AgentsClientSDK.Android

We make it easy for you to have multi modal interactions with the agents created through Microsoft
Copilot Studio (MCS) and Agents SDK.

You can now text and talk to your agent. There are exciting new updates coming up.

Currently, our SDK is only available for private preview and will have to be included as a local
build dependency. We will very soon be available on major package managers/ repositories - ex. Maven

Follow along to add the Android SDK to your app for multimodal agent interactions.

## Getting started with Android

This tutorial will help you connect with an agent created in Copilot Studio and published to a
custom website or mobile app, without authentication. The SDK connects agents using Direct line
protocol, which enables anonymous text-based agent interactions through web sockets.

To ensure a smooth and successful integration of the SDK with your application, please make sure
your development environment meets the following prerequisites.

### Supported Device And OS version

- **Minimum supported Android OS version:** 7 Nougat (API level 24)
- **Recommended Android OS version:** 11.0 Red Velvet Cake (API level 30) or higher
- **Recommended device:** Any Android device with a minimum of 4GB RAM and a quad-core processor
  for optimal performance.

### Dev Env Prerequisites

- **Java version:** 11
- **Kotlin version:** 1.9.0 or higher
- **Gradle version:** 7.5.1 or higher
- **Android Gradle Plugin:** 8.1.0 or higher
- **IDE:** Android Studio Giraffe (2022.3.1) or newer

### Once you have created a new application or project in Android Studio, follow these steps to add the AgentsClientSDK to your project.

### Step 1: Add the SDK to Your Project Build Configuration

This step ensures your Android project can locate and use the AgentsClientSDK library. By updating
your build configuration, you allow Gradle to find the SDK (stored locally or downloaded) and
include it in your app. This is necessary because the SDK is currently distributed as a local SDK
file, not through public repositories like Maven Central. Proper setup here ensures all required
dependencies are available for your app to compile and run the SDK features.

#### In your project's settings.gradle.kts file, make the following changes to use the library or SDK from the app/libs folder.

```
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        ...
        flatDir {
            dirs("app/libs")
        }
    }
}
```

#### In the app's build.gradle.kts file, add the following to automatically download the library from the release that matches the specified sdkVersion, along with the necessary dependencies.

```
val sdkVersion = "v1.0.0"
task("downloadSdkFiles") {
    doLast {
        println("Download SDK files task started...")
        val url =
            "https://github.com/microsoft/AgentsClientSDK.Android/releases/download/$sdkVersion/AgentsClientSDK.jar"
        val sdkFile = file("${project.rootDir}/app/libs/AgentsClientSDK.jar")
        sdkFile.parentFile.mkdirs() // Ensure directory exists
        URL(url).openStream()
            .use { input -> sdkFile.outputStream().use { output -> input.copyTo(output) } }
    }
}

tasks.named("preBuild") {
    dependsOn("downloadSdkFiles")
}
```

Dependencies for the SDK are as follows:

```
val ktorVersion = "2.3.2"
implementation(files("libs/AgentsClientSDK.jar"))
implementation("androidx.appcompat:appcompat:1.6.1")
implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
implementation("com.microsoft.cognitiveservices.speech:client-sdk:1.41.1")
implementation("com.google.code.gson:gson:2.8.9")
implementation("io.adaptivecards:adaptivecards-android:3.6.1")
```

### Step 2: Import the SDK Classes Needed for Integration

In this step, you import the essential classes from the AgentsClientSDK into your main activity.
This allows your application to access the SDK’s core functionality and configuration models,
enabling you to initialize and interact with the SDK in your app’s code. Proper imports are required
for successful compilation and usage of the SDK features.

``` 
import com.microsoft.agents.client.android.AgentsClientSDK
import com.microsoft.agents.client.android.models.AppSettings
import com.microsoft.agents.client.android.sdks.ClientSDK
```

### Step 3: Configure the SDK with appsettings.json

This step guides you to create an `appsettings.json` file in your `res/raw` directory. This
configuration file provides the necessary environment, agent, and speech settings required by the
SDK to connect and function correctly. The file should look like this:

```json
{
  "user": {
    "environmentId": "",        // environment in which agent is created
    "schemaName": "",           // schema name of agent. Both are available in agent Metadata
    "environment": "",          // mapping given below
    "isAuthEnabled": false,     // remains false for this release
    "auth": {                   // furure scope. No need to input anything for now
      "clientId": "",
      "tenantId": "",
      "redirectUri": ""
    }
  },
  "speech": {               // furure scope. No need to input anything for now
    "speechSubscriptionKey": "",
    "speechServiceRegion": ""
  }
}
```

Environment mapping:

```
copilotstudio.microsoft.com -> prod
copilotstudio.preview.microsoft.com -> prod
copilotstudio.preprod.microsoft.com -> preprod
```

### Step 4: Initialize the SDK Connection in Your Main Activity

This step demonstrates how to load your configuration from `appsettings.json` and initialize the
AgentsClientSDK in your main activity. Proper initialization ensures the SDK is ready to connect to
your agent and handle user interactions as soon as your app starts. This setup is essential for
enabling communication between your app and the agent using the provided settings.

``` 
fun loadAppSettings(context: Context): AppSettings {
    val inputStream = context.resources.openRawResource(R.raw.appsettings)
    val json = inputStream.bufferedReader().use { it.readText() }
    return Gson().fromJson(json, AppSettings::class.java)
}

val appSettings = loadAppSettings(this)
```

The below sample demonstrates how to initialize the AgentsClientSDK, typically in your onCreate
method of MainActivity. You can initialize the SDK from any place in your app where it is
required—this is just a starting point.

During initialization, the SDK requires two parameters:
applicationContext (here passed as this@MainActivity): Needed for the SDK to manage UI state and
render adaptive cards correctly.
appSettings: The configuration object created in the previous step, essential for the SDK’s core
functionality.

```
val agentsClientSdk = AgentsClientSDK.init(this@MainActivity, appSettings)
```

### Step 5: Displaying Chat Messages Using State Flow in the UI

This step explains how to observe and display chat messages exchanged between the user and the agent
in your app’s UI. The SDK exposes a `liveData` property as a `StateFlow`, which emits updates
whenever a new message is sent or received. By collecting this state in your UI (e.g., using
`collectAsState()` in Compose), you can reactively update the chat window with the latest messages.

The SDK uses a sealed class `MessageResponse` to represent different message states, and each
message is encapsulated in a `ChatMessage` object, which includes the message text and the sender
(user or agent). This approach ensures your UI always reflects the current conversation state.

Example for observing message updates:

``` 
val messageResponse by (agentsClientSdk?.liveData?.collectAsState()
    ?: remember { mutableStateOf(MessageResponse.Initial) })
```

Example UI update based on state flow

```
is MessageResponse.Success -> {
    val messageData = (messageResponse as MessageResponse.Success<ChatMessage>).value
    messages = if (messageData.role == "bot") {
        listOf(
            UiChatMessage(
                id = "bot-${System.currentTimeMillis()}",
                message = messageData.text,
                isUser = false,
                isAgentTyping = false
            )
        ) + messages.filterNot { it.isAgentTyping }
    } else {
        listOf(
            UiChatMessage(
                id = "user-${System.currentTimeMillis()}",
                message = messageData.text,
                isUser = true,
                isAgentTyping = false
            )
        ) + messages.filterNot { it.isAgentTyping }
    }
}
```

### Step 6: Sending Messages to the Agent

This step demonstrates how to send a message from your app to the agent using the SDK. The
`sendMessage` function is called on the `agentsClientSdk` instance, passing the user's input text.
This triggers the SDK to forward the message to the agent and handle the response, which will be
reflected in the chat UI if you are observing the `liveData` as shown in previous steps.

This is a simple, direct way to send user input to the agent. You can call this method from any part
of your app where you want to initiate a conversation or respond to user actions.

```
agentsClientSdk.sendMessage(text)
```

That’s it—you’re all set to start building engaging multimodal agent experiences in your Android
app! For a complete, working example, check out the `TextClientApp` in the `samples` folder of this
repository.

## Contributing

This project welcomes contributions and suggestions. Most contributions require you to agree to a
Contributor License Agreement (CLA) declaring that you have the right to, and actually do, grant us
the rights to use your contribution. For details, visit https://cla.opensource.microsoft.com.

When you submit a pull request, a CLA bot will automatically determine whether you need to provide
a CLA and decorate the PR appropriately (e.g., status check, comment). Simply follow the
instructions
provided by the bot. You will only need to do this once across all repos using our CLA.

This project has adopted
the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/).
For more information see
the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or
contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or
comments.

## Trademarks

This project may contain trademarks or logos for projects, products, or services. Authorized use of
Microsoft
trademarks or logos is subject to and must follow
[Microsoft's Trademark & Brand Guidelines](https://www.microsoft.com/en-us/legal/intellectualproperty/trademarks/usage/general).
Use of Microsoft trademarks or logos in modified versions of this project must not cause confusion
or imply Microsoft sponsorship.
Any use of third-party trademarks or logos are subject to those third-party's policies.