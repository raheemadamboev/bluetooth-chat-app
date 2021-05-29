package xyz.teamgravity.bluetoothchat.helper.adapter.chat

import androidx.recyclerview.widget.RecyclerView
import xyz.teamgravity.bluetoothchat.databinding.CardRemoteMessageBinding
import xyz.teamgravity.bluetoothchat.model.MessageModel

class RemoteMessageViewHolder(private val binding: CardRemoteMessageBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(message: MessageModel.RemoteMessage) {
        binding.messageText.text = message.text
    }
}