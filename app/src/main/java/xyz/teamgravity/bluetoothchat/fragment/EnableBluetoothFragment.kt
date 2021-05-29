package xyz.teamgravity.bluetoothchat.fragment

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import xyz.teamgravity.bluetoothchat.databinding.FragmentEnableBluetoothBinding
import xyz.teamgravity.bluetoothchat.helper.constants.REQUEST_ENABLE_BT
import xyz.teamgravity.bluetoothchat.helper.util.ChatServer

class EnableBluetoothFragment : Fragment() {

    private var _binding: FragmentEnableBluetoothBinding? = null
    private val binding get() = _binding!!

    private val bluetoothEnableObserver = Observer<Boolean> { shouldPrompt ->
        if (!shouldPrompt) {
            // Don't need to prompt so navigate to LocationRequiredFragment
            findNavController().navigate(EnableBluetoothFragmentDirections.actionCheckLocationPermissions())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ChatServer.requestEnableBluetooth.observe(this, bluetoothEnableObserver)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEnableBluetoothBinding.inflate(inflater, container, false)

        binding.errorAction.setOnClickListener {
            // Prompt user to turn on Bluetooth (logic continues in onActivityResult()).
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_ENABLE_BT -> {
                if (resultCode == Activity.RESULT_OK) {
                    ChatServer.startServer(requireActivity().application)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}