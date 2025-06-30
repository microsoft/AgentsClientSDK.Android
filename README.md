# Welcome to AgentsClientSDK.Android

We make it easy for you to have multi modal interactions with the agents created through Microsoft
Copilot Studio (MCS) and Agents SDK.

You can now text and talk to your agent and seamlessly switch between the two.
The agent is continuously listening (CL) and can be interrupted any time.
There are exciting new updates coming up. Voice is just the beginning!

Currently, our SDK is only available for private preview and will have to be included as a local
build dependency. We will very soon be available on major package managers/ repositories - ex. Maven

Follow along to add the Android SDK to your app for multimodal agent interactions.

## Getting started with Android

This tutorial will help you connect with an agent created in Copilot Studio and published to custom
website or mobile app, without authentication.
The SDK connects to agents using Directline protocol, which enables anonymous text based agent
interactions through websockets.
You will need the following:

1. Bot schema name
2. Environment Id

### Step 1: Include in build

Include the MultimodalClientSDK.aar file as a Gradle dependency, along with the following
dependencies

```
val ktorVersion = "2.3.2"
implementation(mapOf("name" to "MultimodalClientSDK", "ext" to "aar"))
implementation("androidx.appcompat:appcompat:1.6.1")
implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
implementation("com.microsoft.cognitiveservices.speech:client-sdk:1.41.1")
implementation("com.google.code.gson:gson:2.8.9")
implementation("io.adaptivecards:adaptivecards-android:3.6.1")
```

### Step 2: Import multimodal classes in your main activity

``` 
import com.microsoft.multimodal.clientsdk.MultimodalClientSDK
import com.microsoft.multimodal.clientsdk.configs.SDKConfigs
import com.microsoft.multimodal.clientsdk.models.ChatMessage
import com.microsoft.multimodal.clientsdk.models.MessageResponse
```

### Step 3: Connection to SDK is initialized like so

``` 
MultimodalClientSDK.init(this@MainActivity, sdkConfigs)
```

### Step 4: Chat window for viewing text

Your users can see the text version of the exchange.
This can be done by ClientSDK.liveData state flow.
The SDK updates user queries and responses through a MutableStateFlow. It has definite states
defined by the sealed class ```MessageResponse``` and gets updated through ```ChatMessage ```
generic android view. ChatMessage provides the text ( user query or agent response) and the
speaker (user or agent) values.

Declared like this

``` val messageResponse by MultimodalClientSDK.liveData.collectAsState()```

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

Thats the essence of it.
The Sample Application in samples folder of this repository provides a complete implementation. Do
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