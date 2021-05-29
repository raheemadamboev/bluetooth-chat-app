package xyz.teamgravity.bluetoothchat.helper.adapter.scan

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import xyz.teamgravity.bluetoothchat.databinding.CardDeviceBinding

class DeviceScanAdapter(private val onDeviceSelected: (BluetoothDevice) -> Unit) : RecyclerView.Adapter<DeviceScanViewHolder>() {

    private var items = listOf<BluetoothDevice>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceScanViewHolder =
        DeviceScanViewHolder(CardDeviceBinding.inflate(LayoutInflater.from(parent.context), parent, false), onDeviceSelected)


    override fun onBindViewHolder(holder: DeviceScanViewHolder, position: Int) {
        items.getOrNull(position)?.let { result ->
            holder.bind(result)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateItems(results: List<BluetoothDevice>) {
        items = results
        notifyDataSetChanged()
    }
}