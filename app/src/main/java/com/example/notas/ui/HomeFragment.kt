package com.example.notas.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.notas.R
import com.example.notas.databinding.FragmentCadastroBinding
import com.example.notas.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel

    private var isLocationPermissionGranted = false
    private var isCameraPermissionGranted = false

    private lateinit var singlePermissionLauncher: ActivityResultLauncher<String>
    private lateinit var multiplePermissionsLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        setupPermissionsLaunchers()

        binding.btnAddNotaH.setOnClickListener {
            verifyPermissions()
        }

        binding.btnSairH.setOnClickListener {
            viewModel.deslogar()
            val direction = HomeFragmentDirections.actionHomeFragmentToLoginFragment()
            findNavController().navigate(direction)
        }

        binding.tvEmailH.text = viewModel.retornaUsuario()!!.email.toString()

        return view
    }

    fun setupPermissionsLaunchers() {
        singlePermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission())
        { isGranted: Boolean ->
            if (isGranted) {
                toast("Permissão concedida")
            } else {
                toast("Permissão negada")
            }
        }

        multiplePermissionsLauncher = registerForActivityResult(
            ActivityResultContracts
                .RequestMultiplePermissions()
        ) { permissions ->
            isCameraPermissionGranted = permissions[Manifest.permission.CAMERA]
                ?: isCameraPermissionGranted

            isLocationPermissionGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION]
                ?: isLocationPermissionGranted

        }
    }

    fun verifyPermissions(){
        isCameraPermissionGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

        isLocationPermissionGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

        val permissionRequest: MutableList<String> = ArrayList()
        if(!isCameraPermissionGranted){
            permissionRequest.add(Manifest.permission.CAMERA)
        }

        if(!isLocationPermissionGranted){
            permissionRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if(permissionRequest.isNotEmpty()){

            when{
                // Se não tem as duas permissões, pede as duas
                (!isCameraPermissionGranted && !isLocationPermissionGranted) -> {
                    askCameraLocationRationale(permissionRequest)
                }
                // Se não tem a permissão da localização
                (isCameraPermissionGranted && !isLocationPermissionGranted) -> {
                    askLocationRationale()
                }
                // Se não tem a permissão da camera
                (!isCameraPermissionGranted && isLocationPermissionGranted) -> {
                    askCameraRationale()
                }
            }
        } else {
            findNavController().navigate(R.id.newNotaFragment)
        }
    }



    private fun askCameraRationale() {
        showSnackBar(
            binding.frameLayout2,
            getString(R.string.permission_camera_required),
            Snackbar.LENGTH_INDEFINITE,
            getString(R.string.ok)
        ){
            singlePermissionLauncher.launch(
                Manifest.permission.CAMERA
            )
        }
    }


    private fun askLocationRationale() {
        showSnackBar(
            binding.frameLayout2,
            getString(R.string.permission_location_required),
            Snackbar.LENGTH_INDEFINITE,
            getString(R.string.ok)
        ){
            singlePermissionLauncher.launch(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }
    fun showSnackBar(
        view: View,
        msg: String,
        length: Int,
        actionMessage: CharSequence?,
        action: (View) -> Unit
    ){
        val snackBar = Snackbar.make(view, msg, length)

        if (actionMessage !=null){
            snackBar.setAction(actionMessage){
                action(requireView())
            }.show()
        } else {
            snackBar.show()
        }
    }

    fun toast(msg: String){
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    private fun askCameraLocationRationale(permissionRequest: MutableList<String>) {
        showSnackBar(
            binding.frameLayout2,
            getString(R.string.permission_camera_location_required),
            Snackbar.LENGTH_INDEFINITE,
            getString(R.string.ok)
        ){
            multiplePermissionsLauncher.launch(
                permissionRequest.toTypedArray()
            )
        }
    }
}