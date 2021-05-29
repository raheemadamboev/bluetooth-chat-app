package xyz.teamgravity.bluetoothchat.helper.adapter.scan

import android.bluetooth.BluetoothDevice
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import xyz.teamgravity.bluetoothchat.databinding.CardDeviceBinding

class DeviceScanViewHolder(
    private val binding: CardDeviceBinding,
    private val onDeviceSelected: (BluetoothDevice) -> Unit
) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    private var bluetoothDevice: BluetoothDevice? = null

    init {
        itemView.setOnClickListener(this)
    }

    fun bind(device: BluetoothDevice) {
        bluetoothDevice = device
        binding.apply {
            deviceName.text = device.name
            deviceAddress.text = device.address
        }
    }

    override fun onClick(view: View) {
        bluetoothDevice?.let { device ->
            onDeviceSelected(device)
        }
    }
}