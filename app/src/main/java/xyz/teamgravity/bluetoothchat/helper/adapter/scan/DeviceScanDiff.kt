package xyz.teamgravity.bluetoothchat.helper.adapter.scan

import android.bluetooth.BluetoothDevice
import androidx.recyclerview.widget.DiffUtil

class DeviceScanDiff : DiffUtil.ItemCallback<BluetoothDevice>() {

    override fun areItemsTheSame(oldItem: BluetoothDevice, newItem: BluetoothDevice) =
        oldItem.address == newItem.address

    override fun areContentsTheSame(oldItem: BluetoothDevice, newItem: BluetoothDevice) =
        oldItem == newItem
}