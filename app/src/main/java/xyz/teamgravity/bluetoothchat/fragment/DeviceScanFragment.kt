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

    private val deviceScanAdapter by lazy {
        DeviceScanAdapter(onDeviceSelected)
    }

    private val viewStateObserver = Observer<DeviceScanViewState> { state ->
        when (state) {
            is DeviceScanViewState.ActiveScan -> showLoading()
            is DeviceScanViewState.ScanResults -> showResults(state.scanResults)
            is DeviceScanViewState.Error -> showError(state.message)
            is DeviceScanViewState.AdvertisementNotSupported -> showAdvertisingError()
        }.exhaustive
    }

    private val onDeviceSelected: (BluetoothDevice) -> Unit = { device ->
        ChatServer.setCurrentChatConnection(device)
        // navigate back to chat fragment
        findNavController().popBackStack()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDeviceScanBinding.inflate(inflater, container, false)

        val devAddress = getString(R.string.your_device_address) + ChatServer.getYourDeviceAddress()
        binding.yourDeviceAddress.text = devAddress
        binding.deviceList.adapter = deviceScanAdapter

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        requireActivity().setTitle(R.string.device_list_title)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.viewState.observe(viewLifecycleOwner, viewStateObserver)
    }

    private fun showLoading() {
        log(TAG, "showLoading")
        binding.scanningLayout.visible()

        binding.deviceList.gone()
        binding.noDevicesText.gone()
        binding.error.gone()
        binding.chatConfirmContainer.gone()
    }

    private fun showResults(scanResults: Map<String, BluetoothDevice>) {
        if (scanResults.isNotEmpty()) {
            binding.deviceList.visible()
            deviceScanAdapter.submitList(scanResults.values.toList())

            binding.scanningLayout.gone()
            binding.noDevicesText.gone()
            binding.error.gone()
            binding.chatConfirmContainer.gone()
        } else {
            showNoDevices()
        }
    }

    private fun showNoDevices() {
        binding.noDevicesText.visible()

        binding.deviceList.gone()
        binding.scanningLayout.gone()
        binding.error.gone()
        binding.chatConfirmContainer.gone()
    }

    private fun showError(message: String) {
        log(TAG, "showError: $message")
        binding.error.visible()
        binding.errorMessage.text = message

        // hide the action button if one is not provided
        binding.errorAction.gone()
        binding.scanningLayout.gone()
        binding.noDevicesText.gone()
        binding.chatConfirmContainer.gone()
    }

    private fun showAdvertisingError() {
        showError("BLE advertising is not supported on this device")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}