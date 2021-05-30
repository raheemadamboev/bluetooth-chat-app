package xyz.teamgravity.bluetoothchat.helper.adapter.chat

import androidx.recyclerview.widget.RecyclerView
import xyz.teamgravity.bluetoothchat.databinding.CardLocalMessageBinding
import xyz.teamgravity.bluetoothchat.model.MessageModel

class LocalMessageViewHolder(private val binding: CardLocalMessageBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(message: MessageModel.LocalMessage) {
        binding.messageT.text = message.text
    }
}