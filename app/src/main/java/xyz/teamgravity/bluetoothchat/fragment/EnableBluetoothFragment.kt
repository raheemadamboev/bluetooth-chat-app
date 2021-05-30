package xyz.teamgravity.bluetoothchat.fragment

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import xyz.teamgravity.bluetoothchat.databinding.FragmentEnableBluetoothBinding
import xyz.teamgravity.bluetoothchat.helper.util.ChatServer

class EnableBluetoothFragment : Fragment() {

    private var _binding: FragmentEnableBluetoothBinding? = null
    private val binding get() = _binding!!

    private lateinit var bluetoothLauncher: ActivityResultLauncher<Intent>

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
            bluetoothLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        }

        bluetoothLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                ChatServer.startServer(requireActivity().application)
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}