package xyz.teamgravity.bluetoothchat.helper.util

import android.bluetooth.BluetoothDevice

sealed class DeviceScanViewState {
    object ActiveScan: DeviceScanViewState()
    data class ScanResults(val scanResults: Map<String, BluetoothDevice>): DeviceScanViewState()
    data class Error(val message: String): DeviceScanViewState()
    object AdvertisementNotSupported: DeviceScanViewState()
}
