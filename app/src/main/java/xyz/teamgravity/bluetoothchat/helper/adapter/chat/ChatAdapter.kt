package xyz.teamgravity.bluetoothchat.helper.adapter.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import xyz.teamgravity.bluetoothchat.databinding.CardLocalMessageBinding
import xyz.teamgravity.bluetoothchat.databinding.CardRemoteMessageBinding
import xyz.teamgravity.bluetoothchat.helper.extension.log
import xyz.teamgravity.bluetoothchat.model.MessageModel

private const val TAG = "MessageAdapter"
private const val REMOTE_MESSAGE = 0
private const val LOCAL_MESSAGE = 1

class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val messages = mutableListOf<MessageModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        log(TAG, "onCreateViewHolder: ")
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            REMOTE_MESSAGE -> RemoteMessageViewHolder(CardRemoteMessageBinding.inflate(inflater, parent, false))
            LOCAL_MESSAGE -> LocalMessageViewHolder(CardLocalMessageBinding.inflate(inflater, parent, false))
            else -> throw IllegalArgumentException("Unknown MessageAdapter view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        log(TAG, "onBindViewHolder: ")
        when (val message = messages[position]) {
            is MessageModel.RemoteMessage -> {
                (holder as RemoteMessageViewHolder).bind(message)
            }
            is MessageModel.LocalMessage -> {
                (holder as LocalMessageViewHolder).bind(message)
            }
        }
    }

    override fun getItemCount(): Int {
        log(TAG, "getItemCount: ")
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        log(TAG, "getItemViewType: ")
        return when (messages[position]) {
            is MessageModel.RemoteMessage -> REMOTE_MESSAGE
            is MessageModel.LocalMessage -> LOCAL_MESSAGE
        }
    }

    // Add messages to the top of the list so they're easy to see
    fun addMessage(message: MessageModel) {
        log(TAG, "addMessage: ")
        messages.add(0, message)
        notifyDataSetChanged()
    }
}