package xyz.teamgravity.bluetoothchat.fragment

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import xyz.teamgravity.bluetoothchat.R
import xyz.teamgravity.bluetoothchat.databinding.FragmentBluetoothChatBinding
import xyz.teamgravity.bluetoothchat.helper.adapter.chat.ChatAdapter
import xyz.teamgravity.bluetoothchat.helper.chat.DeviceConnectionState
import xyz.teamgravity.bluetoothchat.helper.extension.gone
import xyz.teamgravity.bluetoothchat.helper.extension.log
import xyz.teamgravity.bluetoothchat.helper.extension.visible
import xyz.teamgravity.bluetoothchat.helper.server.ChatServer
import xyz.teamgravity.bluetoothchat.model.MessageModel

private const val TAG = "BluetoothChatFragment"

class BluetoothChatFragment : Fragment() {

    private var _binding: FragmentBluetoothChatBinding? = null
    private val binding get() = _binding!!

    private val deviceConnectionObserver = Observer<DeviceConnectionState> { state ->
        when (state) {
            is DeviceConnectionState.Connected -> {
                val device = state.device
                log(TAG, "Gatt connection observer: have device $device")
                chatWith(device)
            }
            is DeviceConnectionState.Disconnected -> {
                showDisconnected()
            }
        }
    }

    private val connectionRequestObserver = Observer<BluetoothDevice> { device ->
        log(TAG, "Connection request observer: have device $device")
        ChatServer.setCurrentChatConnection(device)
    }

    private val messageObserver = Observer<MessageModel> { message ->
        log(TAG, "Have message ${message.text}")
        adapter.addMessage(message)
    }

    private val adapter = ChatAdapter()

    private val inputMethodManager by lazy {
        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBluetoothChatBinding.inflate(inflater, container, false)

        log(TAG, "chatWith: set adapter $adapter")
        binding.recyclerView.adapter = adapter

        showDisconnected()

        binding.connectDevicesB.setOnClickListener {
            findNavController().navigate(BluetoothChatFragmentDirections.actionFindNewDevice())
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        requireActivity().setTitle(R.string.chat_title)
        ChatServer.connectionRequest.observe(viewLifecycleOwner, connectionRequestObserver)
        ChatServer.deviceConnection.observe(viewLifecycleOwner, deviceConnectionObserver)
        ChatServer.messages.observe(viewLifecycleOwner, messageObserver)
    }

    private fun chatWith(device: BluetoothDevice) {
        binding.connectedContainer.visible()
        binding.notConnectedContainer.gone()

        val chattingWithString = resources.getString(R.string.chatting_with_device, device.address)
        binding.connectedDeviceNameT.text = chattingWithString
        binding.sendMessageB.setOnClickListener {
            val message = binding.messageField.text.toString()
            // only send message if it is not empty
            if (message.isNotEmpty()) {
                ChatServer.sendMessage(message)
                // clear message
                binding.messageField.setText("")
            }
        }
    }

    private fun showDisconnected() {
        hideKeyboard()
        binding.notConnectedContainer.visible()
        binding.connectedContainer.gone()
    }

    private fun hideKeyboard() {
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}