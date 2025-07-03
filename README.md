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

You will need the following:

1. Bot schema name
2. Environment Id
3. Environment

### Once you have created a new application or project in Android Studio, follow these steps to add the AgentsClientSDK to your project.

### Step 1: Include in build

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
task("downloadAarFiles") {
    doLast {
        println("Download AARs task started...")
        val aarUrl =
            "https://github.com/microsoft/AgentsClientSDK.Android/releases/download/$sdkVersion/AgentsClientSDK.aar"
        val aarFile = file("${project.rootDir}/app/libs/AgentsClientSDK.aar")
        aarFile.parentFile.mkdirs() // Ensure directory exists
        URL(aarUrl).openStream()
            .use { input -> aarFile.outputStream().use { output -> input.copyTo(output) } }
    }
}

tasks.named("preBuild") {
    dependsOn("downloadAarFiles")
}
```

Dependencies for the SDK are as follows:

```
val ktorVersion = "2.3.2"
implementation(mapOf("name" to "AgentsClientSDK", "ext" to "aar"))
implementation("androidx.appcompat:appcompat:1.6.1")
implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
implementation("com.microsoft.cognitiveservices.speech:client-sdk:1.41.1")
implementation("com.google.code.gson:gson:2.8.9")
implementation("io.adaptivecards:adaptivecards-android:3.6.1")
```

### Step 2: Import AgentsClientSDK classes in your main activity

``` 
import com.microsoft.agentsclientsdk.AgentsClientSDK
import com.microsoft.agentsclientsdk.models.AppSettings
```

### Step 3: Create appsettings.json file to configure the SDK

Create a file named `appsettings.json` in the `res/raw` folder of your Android project/app. This
file will contain the configuration for the SDK. In case of Directline protocol, the file should
look like this:

```json
{
  "user": {
    "environmentId": "your-environment-id",
    "schemaName": "your-bot-schema-name",
    "environment": "your-environment",
    "auth": {
      "clientId": "",
      "tenantId": ""
    }
  },
  "speech": {
    "speechSubscriptionKey": "",
    "speechServiceRegion": ""
  }
}
```

### Step 4: Connection to SDK is initialized after the appsettings.json file is created and fetched in main activity, like so

``` 
fun loadAppSettings(context: Context): AppSettings {
    val inputStream = context.resources.openRawResource(R.raw.appsettings)
    val json = inputStream.bufferedReader().use { it.readText() }
    return Gson().fromJson(json, AppSettings::class.java)
}

val appSettings = loadAppSettings(this)

AgentsClientSDK.init(this@MainActivity, appSettings)
```

### Step 5: Chat window for viewing text

Your users can see the text version of the exchange.
This can be done by ClientSDK.liveData state flow.
The SDK updates user queries and responses through a MutableStateFlow. It has definite states
defined by the sealed class ```MessageResponse``` and gets updated through ```ChatMessage ```
generic android view. ChatMessage provides the text (user query or agent response) and the
speaker (user or agent) values.

Declared like this

``` 
val messageResponse by AgentsClientSDK.liveData.collectAsState()
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

### Step 6: Send Message

Below is example on how you can send message to bot

```
AgentsClientSDK.sdk?.sendMessage(text)
```

Thats the essence of it.
The TextClientApp in samples folder of this repository provide a complete implementation. Do
check it out.

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