package xyz.teamgravity.bluetoothchat.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import xyz.teamgravity.bluetoothchat.databinding.FragmentLocationRequiredBinding
import xyz.teamgravity.bluetoothchat.helper.extension.log

class LocationRequiredFragment : Fragment() {
    companion object {
        private const val TAG = "LocationRequiredFrag:"
        private const val PERMISSIONS_REQUEST_CODE = 1
        private val PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

        @RequiresApi(Build.VERSION_CODES.Q)
        private val PERMISSIONS_Q = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )
    }

    private var _binding: FragmentLocationRequiredBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLocationRequiredBinding.inflate(inflater, container, false)

        // setup click listener on grant permission button
        binding.grantPermissionButton.setOnClickListener {
            checkLocationPermission()
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        // check location permission when the Fragment becomes visible on screen
        checkLocationPermission()
    }

    private fun showError() {
        binding.locationErrorMessage.visibility = View.VISIBLE
        binding.grantPermissionButton.visibility = View.VISIBLE
    }

    // check permissions
    private fun hasPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            PERMISSIONS.all {
                ActivityCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
            }
        } else {
            PERMISSIONS_Q.all {
                ActivityCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
            }
        }
    }

    // request permissions
    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS, PERMISSIONS_REQUEST_CODE)
        } else {
            ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS_Q, PERMISSIONS_REQUEST_CODE)
        }
    }

    private fun checkLocationPermission() {
        if (hasPermissions()) {
            findNavController().navigate(LocationRequiredFragmentDirections.actionStartChat())
        } else {
            requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        log(TAG, "onRequestPermissionsResult: ")
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    findNavController().navigate(LocationRequiredFragmentDirections.actionStartChat())
                } else {
                    showError()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}