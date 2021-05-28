package xyz.teamgravity.bluetoothchat.helper.chat

import android.bluetooth.BluetoothDevice

sealed class DeviceConnectionState {
    data class Connected(val device: BluetoothDevice) : DeviceConnectionState()
    object Disconnected : DeviceConnectionState()
}