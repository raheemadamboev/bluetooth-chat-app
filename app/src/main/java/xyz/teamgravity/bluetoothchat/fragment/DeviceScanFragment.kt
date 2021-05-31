package xyz.teamgravity.bluetoothchat.fragment

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import xyz.teamgravity.bluetoothchat.R
import xyz.teamgravity.bluetoothchat.arch.DeviceScanViewModel
import xyz.teamgravity.bluetoothchat.databinding.FragmentDeviceScanBinding
import xyz.teamgravity.bluetoothchat.helper.adapter.scan.DeviceScanAdapter
import xyz.teamgravity.bluetoothchat.helper.extension.exhaustive
import xyz.teamgravity.bluetoothchat.helper.extension.gone
import xyz.teamgravity.bluetoothchat.helper.extension.log
import xyz.teamgravity.bluetoothchat.helper.extension.visible
import xyz.teamgravity.bluetoothchat.helper.util.ChatServer
import xyz.teamgravity.bluetoothchat.helper.util.DeviceScanViewState

private const val TAG = "DeviceScanFragment"

class DeviceScanFragment : Fragment() {

    private var _binding: FragmentDeviceScanBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DeviceScanViewModel by viewModels()

    private val deviceScanAdapter by lazy { DeviceScanAdapter(onDeviceSelected) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDeviceScanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.viewState.observe(viewLifecycleOwner, viewStateObserver)
        updateUI()
    }

    private fun updateUI() {
        binding.apply {
            yourDeviceAddressT.text = getString(R.string.your_device_address) + ChatServer.getYourDeviceAddress()
            recyclerView.adapter = deviceScanAdapter
        }
    }

    private fun showLoading() {
        log(TAG, "showLoading")
        binding.apply {
            scanningContainer.visible()

            recyclerView.gone()
            noDevicesText.gone()
            errorT.gone()
        }
    }

    private fun showResults(scanResults: Map<String, BluetoothDevice>) {
        if (scanResults.isNotEmpty()) {
            binding.apply {
                recyclerView.visible()
                deviceScanAdapter.submitList(scanResults.values.toList())

                scanningContainer.gone()
                noDevicesText.gone()
                errorT.gone()
            }
        } else {
            showNoDevices()
        }
    }

    private fun showNoDevices() {
        binding.apply {
            noDevicesText.visible()

            recyclerView.gone()
            scanningContainer.gone()
            errorT.gone()
        }
    }

    private fun showError(message: String) {
        log(TAG, "showError: $message")
        binding.apply {
            errorT.visible()
            errorT.text = message

            scanningContainer.gone()
            noDevicesText.gone()
        }
    }

    private fun showAdvertisingError() {
        showError(getString(R.string.bt_ads_not_supported))
    }

    // device select button
    private val onDeviceSelected: (BluetoothDevice) -> Unit = { device ->
        ChatServer.setCurrentChatConnection(device)
        // navigate back to chat fragment
        findNavController().popBackStack()
    }

    private val viewStateObserver = Observer<DeviceScanViewState> { state ->
        when (state) {
            is DeviceScanViewState.ActiveScan -> showLoading()
            is DeviceScanViewState.ScanResults -> showResults(state.scanResults)
            is DeviceScanViewState.Error -> showError(state.message)
            is DeviceScanViewState.AdvertisementNotSupported -> showAdvertisingError()
        }.exhaustive
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}