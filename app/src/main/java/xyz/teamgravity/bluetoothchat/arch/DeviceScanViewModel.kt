package xyz.teamgravity.bluetoothchat.arch

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.*
import android.os.ParcelUuid
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import xyz.teamgravity.bluetoothchat.helper.constants.SERVICE_UUID
import xyz.teamgravity.bluetoothchat.helper.extension.log
import xyz.teamgravity.bluetoothchat.helper.scan.DeviceScanViewState

private const val TAG = "DeviceScanViewModel"

// 30 second scan period
private const val SCAN_PERIOD = 30_000L

class DeviceScanViewModel(app: Application) : AndroidViewModel(app) {

    // livedata for sending the view state to the DeviceScanFragment
    private val _viewState = MutableLiveData<DeviceScanViewState>()
    val viewState = _viewState as LiveData<DeviceScanViewState>

    // String key is the address of the bluetooth device
    private val scanResults = mutableMapOf<String, BluetoothDevice>()

    // BluetoothAdapter should never be null since BLE is required per
    // the <uses-feature> tag in the AndroidManifest.xml
    private val adapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    // This property will be null if bluetooth is not enabled
    private var scanner: BluetoothLeScanner? = null

    private var scanCallback: DeviceScanCallback? = null
    private val scanFilters: List<ScanFilter>
    private val scanSettings: ScanSettings

    init {
        // Setup scan filters and settings
        scanFilters = buildScanFilters()
        scanSettings = buildScanSettings()

        // Start a scan for BLE devices
        startScan()
    }

    override fun onCleared() {
        super.onCleared()
        stopScanning()
    }

    fun startScan() {
        // If advertisement is not supported on this device then other devices will not be able to
        // discover and connect to it.
        if (!adapter.isMultipleAdvertisementSupported) {
            _viewState.value = DeviceScanViewState.AdvertisementNotSupported
            return
        }

        if (scanCallback == null) {
            scanner = adapter.bluetoothLeScanner
            log(TAG, "Start Scanning")
            // Update the UI to indicate an active scan is starting
            _viewState.value = DeviceScanViewState.ActiveScan

            // Stop scanning after the scan period
            viewModelScope.launch {
                delay(SCAN_PERIOD)
                stopScanning()
            }

            // Kick off a new scan
            scanCallback = DeviceScanCallback()
            scanner?.startScan(scanFilters, scanSettings, scanCallback)
        } else {
            log(TAG, "Already scanning")
        }
    }

    private fun stopScanning() {
        log(TAG, "Stopping Scanning")
        scanner?.stopScan(scanCallback)
        scanCallback = null
        // return the current results
        _viewState.value = DeviceScanViewState.ScanResults(scanResults)
    }

    /**
     * Return a List of [ScanFilter] objects to filter by Service UUID.
     */
    private fun buildScanFilters(): List<ScanFilter> {
        val builder = ScanFilter.Builder()
        // Comment out the below line to see all BLE devices around you
        builder.setServiceUuid(ParcelUuid(SERVICE_UUID))
        val filter = builder.build()
        return listOf(filter)
    }

    /**
     * Return a [ScanSettings] object set to use low power (to preserve battery life).
     */
    private fun buildScanSettings(): ScanSettings {
        return ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
            .build()
    }

    /**
     * Custom ScanCallback object - adds found devices to list on success, displays error on failure.
     */
    private inner class DeviceScanCallback : ScanCallback() {
        override fun onBatchScanResults(results: List<ScanResult>) {
            super.onBatchScanResults(results)
            for (item in results) {
                item.device?.let { device ->
                    scanResults[device.address] = device
                }
            }
            _viewState.value = DeviceScanViewState.ScanResults(scanResults)
        }

        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            result.device?.let { device ->
                scanResults[device.address] = device
            }
            _viewState.value = DeviceScanViewState.ScanResults(scanResults)
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            // Send error state to the fragment to display
            val errorMessage = "Scan failed with error: $errorCode"
            _viewState.value = DeviceScanViewState.Error(errorMessage)
        }
    }
}