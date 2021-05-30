package xyz.teamgravity.bluetoothchat.fragment

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.location.LocationManager
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
import xyz.teamgravity.bluetoothchat.helper.extension.gone
import xyz.teamgravity.bluetoothchat.helper.extension.log
import xyz.teamgravity.bluetoothchat.helper.extension.visible
import xyz.teamgravity.bluetoothchat.helper.util.ChatServer
import xyz.teamgravity.bluetoothchat.helper.util.DeviceConnectionState
import xyz.teamgravity.bluetoothchat.model.MessageModel

private const val TAG = "BluetoothChatFragment"

class BluetoothChatFragment : Fragment() {

    private var _binding: FragmentBluetoothChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var locationManager: LocationManager

    private val adapter = ChatAdapter()

    private val inputMethodManager by lazy {
        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBluetoothChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lateInIt()
        button()
        updateUI()
    }

    override fun onStart() {
        super.onStart()
        observe()
    }

    private fun lateInIt() {
        locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    private fun button() {
        onScan()
        onSendMessage()
    }

    private fun updateUI() {
        binding.recyclerView.adapter = adapter
        showDisconnected()
    }

    private fun observe() {
        ChatServer.connectionRequest.observe(viewLifecycleOwner, connectionRequestObserver)
        ChatServer.deviceConnection.observe(viewLifecycleOwner, deviceConnectionObserver)
        ChatServer.messages.observe(viewLifecycleOwner, messageObserver)
    }

    private fun showDisconnected() {
        hideKeyboard()
        binding.apply {
            notConnectedContainer.visible()
            connectedContainer.gone()
        }
    }

    private fun hasGPSEnabled() = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

    private fun hideKeyboard() = inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)

    private fun prepareChat(device: BluetoothDevice) {
        binding.apply {
            connectedContainer.visible()
            notConnectedContainer.gone()

            val chattingWithString = resources.getString(R.string.chatting_with_device, device.address)
            connectedDeviceNameT.text = chattingWithString
        }
    }

    // scan button
    private fun onScan() {
        binding.connectDevicesB.setOnClickListener {
            if (hasGPSEnabled()) {
                findNavController().navigate(BluetoothChatFragmentDirections.actionFindNewDevice())
            } else {
                startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        }
    }

    // send message button
    private fun onSendMessage() {
        binding.apply {
            sendMessageB.setOnClickListener {
                val message = binding.messageField.text.toString()

                // only send message if it is not empty
                if (message.isNotEmpty()) {

                    ChatServer.sendMessage(message)

                    // clear message
                    messageField.setText("")
                }
            }
        }
    }

    // connected or disconnected observer
    private val deviceConnectionObserver = Observer<DeviceConnectionState> { state ->
        when (state) {
            is DeviceConnectionState.Connected -> {
                log(TAG, "Gatt connection observer: have device ${state.device}")
                prepareChat(state.device)
            }

            is DeviceConnectionState.Disconnected -> {
                showDisconnected()
            }
        }
    }

    // connected bluetooth device observer
    private val connectionRequestObserver = Observer<BluetoothDevice> { device ->
        log(TAG, "Connection request observer: have device $device")
        ChatServer.setCurrentChatConnection(device)
    }

    // new message observer
    private val messageObserver = Observer<MessageModel> { message ->
        log(TAG, "Have message ${message.text}")
        adapter.addMessage(message)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}