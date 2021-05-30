package xyz.teamgravity.bluetoothchat.helper.adapter.scan

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import xyz.teamgravity.bluetoothchat.databinding.CardDeviceBinding

class DeviceScanAdapter(private val onDeviceSelected: (BluetoothDevice) -> Unit) :
    ListAdapter<BluetoothDevice, DeviceScanViewHolder>(DeviceScanDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceScanViewHolder =
        DeviceScanViewHolder(CardDeviceBinding.inflate(LayoutInflater.from(parent.context), parent, false), onDeviceSelected)


    override fun onBindViewHolder(holder: DeviceScanViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}