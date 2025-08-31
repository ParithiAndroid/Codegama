package com.parithidb.cgnews.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.parithidb.cgnews.R
import com.parithidb.cgnews.data.database.AppDatabase
import com.parithidb.cgnews.databinding.FragmentAccountBinding
import com.parithidb.cgnews.ui.login.LoginActivity
import com.parithidb.cgnews.util.SharedPrefHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import javax.inject.Inject

class AccountFragment : Fragment() {

    private lateinit var binding: FragmentAccountBinding
    private lateinit var sharedPref: SharedPrefHelper
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var cameraImageUri: Uri

    // Camera launcher
    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            binding.ivProfilePic.setImageURI(cameraImageUri)
            sharedPref.setProfilePic(cameraImageUri.toString())
            notifyProfilePicChanged()
        }
    }

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let {
            val savedUri = persistImage(it)
            savedUri?.let { localUri ->
                binding.ivProfilePic.setImageURI(localUri)
                sharedPref.setProfilePic(localUri.toString())
                notifyProfilePicChanged()
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        sharedPref = SharedPrefHelper(requireContext())
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvName.setText(sharedPref.getUserName() ?: "John Doe")
        binding.tvEmail.setText(sharedPref.getUserEmail() ?: "johndoe@gmail.com")

        val profilePic = sharedPref.getProfilePic()
        if (profilePic != null) {
            binding.ivProfilePic.setImageURI(Uri.parse(profilePic))
        } else {
            binding.ivProfilePic.setImageDrawable(resources.getDrawable(R.drawable.ic_person))
        }

        // Location
        requestLocation()

        // Change pic
        binding.btnChangePic.setOnClickListener {
            val options = arrayOf("Camera", "Gallery", "Remove")
            AlertDialog.Builder(requireContext())
                .setTitle("Change Profile Picture")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> openCamera()
                        1 -> pickMedia.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                        2 -> {
                            binding.ivProfilePic.setImageResource(R.drawable.ic_person)
                            sharedPref.setProfilePic(null)
                            notifyProfilePicChanged()
                        }
                    }
                }
                .show()
        }


        // Logout
        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton("Yes") { dialog, _ ->
                    sharedPref.clearPreferences()
                    startActivity(Intent(requireContext(), LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                    dialog.dismiss()
                }
                .show()
        }

    }

    private fun openCamera() {
        val imageFile = File(requireContext().cacheDir, "profile_${System.currentTimeMillis()}.jpg")
        cameraImageUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            imageFile
        )
        takePicture.launch(cameraImageUri)
    }

    private fun persistImage(uri: Uri): Uri? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val file = File(requireContext().filesDir, "profile_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file, false)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // First try last location
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    updateLocationUI(location)
                } else {
                    // Request a fresh location if last is null
                    val locationRequest = LocationRequest.Builder(
                        Priority.PRIORITY_HIGH_ACCURACY, 1000
                    ).setMaxUpdates(1).build()

                    fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        object : LocationCallback() {
                            override fun onLocationResult(result: LocationResult) {
                                val freshLocation = result.lastLocation
                                freshLocation?.let { updateLocationUI(it) }
                                fusedLocationClient.removeLocationUpdates(this)
                            }
                        },
                        Looper.getMainLooper()
                    )
                }
            }
        } else {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1001
            )
        }
    }

    private fun updateLocationUI(location: Location) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

                val addressText = if (!addresses.isNullOrEmpty()) {
                    addresses[0].getAddressLine(0)
                } else {
                    "Lat: ${location.latitude}, Lng: ${location.longitude}"
                }

                withContext(Dispatchers.Main) {
                    binding.tvLocation.setText(addressText)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    binding.tvLocation.setText("Lat: ${location.latitude}, Lng: ${location.longitude}")
                }
            }
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            requestLocation()
        }
    }

    private fun notifyProfilePicChanged() {
        (requireActivity() as? AppCompatActivity)?.invalidateOptionsMenu()
    }
}


