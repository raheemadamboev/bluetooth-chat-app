package xyz.teamgravity.bluetoothchat.model

sealed class MessageModel(val text: String) {

    class RemoteMessage(text: String) : MessageModel(text)
    class LocalMessage(text: String) : MessageModel(text)
}
