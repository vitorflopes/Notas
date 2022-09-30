package com.example.notas.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64.encodeToString
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.doOnLayout
import androidx.lifecycle.ViewModelProvider
import com.example.android.criminalintent.utils.getScaledBitmap
import com.example.notas.databinding.FragmentNewNotaBinding
import java.io.*
import java.security.KeyStore
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import android.util.Base64
import androidx.navigation.fragment.findNavController

class NewNotaFragment : Fragment(), LocationListener {

    private var _binding: FragmentNewNotaBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: NewNotaViewModel

    var dataAgora: String? = null
    var fotoTirada = false

    private lateinit var locationManager: LocationManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewNotaBinding.inflate(inflater, container, false)
        val view = binding.root
        viewModel = ViewModelProvider(this).get(NewNotaViewModel::class.java)

        setup()

        return view
    }

    private fun setup() {
        setupView()
        setupClickListeners()
        getLocation()
    }

    private fun setupView() {
        val captureImageIntent = takePhoto.contract.createIntent(
            requireContext(),
            Uri.EMPTY
        )
        binding.btnTirarFotoNN.isEnabled = canResolveIntent(captureImageIntent)

        val formatter = SimpleDateFormat("dd/MM/yyyy - HH:mm")
        val dateNow = Date()
        val mDate = formatter.format(dateNow)
        binding.tvDataNN.text = mDate
    }

    private fun setupClickListeners() {
        binding.btnTirarFotoNN.setOnClickListener {
            val formatter = SimpleDateFormat("dd_MM_yyyy-HH_mm")
            val dateNow = Date()
            dataAgora = formatter.format(dateNow)
            binding.tvDataNN.text = dataAgora

            photoName = "IMG_${dataAgora}.JPG"

            val fullDirName = "${context?.filesDir}/$photoName"

            val photoFile = File(fullDirName)
            val photoUri = FileProvider.getUriForFile(
                requireContext(),
                "com.example.android.notas.fileprovider",
                photoFile
            )
            takePhoto.launch(photoUri)
        }

        binding.btnSalvarNotaNN.setOnClickListener {
            if (validate()) {
                val nomePadrao = binding.etNomeNotaNN.text.toString() + " " + dataAgora
                val nomeArquivoTexto = "$nomePadrao.txt"
                val nomeArquivoFoto = "$nomePadrao.fig"

                val conteudo = binding.tvLocation.text.toString() + "\n" + binding.etTextoNN.text.toString()
                val conteudoCript = Base64.encodeToString(cipher(conteudo), Base64.DEFAULT)
                gravarCriptografado(nomeArquivoTexto, conteudoCript)

                val fullDirNameText = "${context?.filesDir}/$nomeArquivoTexto"
                val fullDirNameFoto = "${context?.filesDir}/$nomeArquivoFoto"
                val photoFile = File(fullDirNameFoto)
                val textFile = File(fullDirNameText)

                findNavController().popBackStack()
            }
        }
    }

    private fun getLocation() {
        locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (
            (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
        }

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            5000,
            5f,
            this

        )
    }

    fun gravarCriptografado(nomeArquivoCompleto: String, conteudo: String) {
        val encryptedOutputStream: FileOutputStream = requireActivity().openFileOutput(nomeArquivoCompleto, Context.MODE_PRIVATE)
        encryptedOutputStream.write(conteudo.toByteArray())
    }

    // Lê arquivo de forma simples
    fun lerCriptografado(nomeArquivoCompleto: String): String {

        // Gera chave mestra para criptografia
        val masterKeyAlias: String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        // cria arquivo
        val file = File(nomeArquivoCompleto)
        // Configura arquivo criptografado
        val encryptedFile: EncryptedFile = EncryptedFile.Builder(
            file,
            requireContext(),
            masterKeyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        val content = StringBuilder()
        try {

            // Na leitura simples é
            // val input = openFileInput(nomeArquivo)
            val input = encryptedFile.openFileInput()
            val reader = BufferedReader(InputStreamReader(input))
            reader.use {
                //  reader.use fecha o reader automaticamente após ler
                reader.forEachLine {
                    // lê todas as linhas e adiciona ao fim de content
                    content.append("$it\n")
                    //Log.i(TAG, it )
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        //Log.i(TAG, content.toString() )
        return content.toString()
    }

    fun validate() : Boolean {
        var resposta = true

        if (binding.etNomeNotaNN.text.toString().isNullOrBlank() || binding.etTextoNN.text.toString().isNullOrBlank()) {
            Toast.makeText(requireContext(), "Preencha todos os campos", Toast.LENGTH_LONG).show()
            resposta = false
        }

        if (!fotoTirada) {
            Toast.makeText(requireContext(), "Foto não tirada", Toast.LENGTH_LONG).show()
            resposta = false
        }

        return resposta
    }

    private var photoName: String? = null
    private val takePhoto = registerForActivityResult(ActivityResultContracts.TakePicture())
    { didTakePhoto: Boolean,  ->

        if (didTakePhoto && photoName != null) {
            updatePhoto(photoName)
        }
    }

    private fun canResolveIntent(intent: Intent): Boolean {
        val packageManager: PackageManager = requireContext().packageManager
        val resolvedActivity: ResolveInfo? =
            packageManager.resolveActivity(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
        return resolvedActivity != null
    }

    private fun updatePhoto(photoFileName: String?) {
        val fullDirName = "${context?.filesDir}/$photoName"
        val photoFile = File(fullDirName)

        if (photoFile.exists()) {
            binding.btnTirarFotoNN.doOnLayout { measuredView ->
                val scaledBitmap = getScaledBitmap(
                    photoFile.path,
                    measuredView.width,
                    measuredView.height
                )
                binding.btnTirarFotoNN.setImageBitmap(scaledBitmap)
                binding.btnTirarFotoNN.tag = photoFileName
            }
        } else {
            binding.btnTirarFotoNN.setImageBitmap(null)
            binding.btnTirarFotoNN.tag = null
        }

        fotoTirada = true
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun cipher(original: String): ByteArray {

        val c = Criptografador()

        val chave = c.getSecretKey()
        return cipher(original,chave)
    }

    fun cipher(original: String, chave: SecretKey?): ByteArray {
        if (chave != null) {
            Cipher.getInstance("AES/CBC/PKCS7Padding").run {
                init(Cipher.ENCRYPT_MODE,chave)
                val valorCripto = doFinal(original.toByteArray())
                val ivCripto = ByteArray(16)
                iv.copyInto(ivCripto,0,0,16)
                return ivCripto + valorCripto
            }
        } else return byteArrayOf()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun decipher(cripto: ByteArray): String{
        val c = Criptografador()

        val chave = c.getSecretKey()
        return decipher(cripto,chave)
    }

    fun decipher(cripto: ByteArray, chave: SecretKey?): String{
        if (chave != null) {
            Cipher.getInstance("AES/CBC/PKCS7Padding").run {
                val ivCripto = ByteArray(16)
                var valorCripto = ByteArray(cripto.size-16)
                cripto.copyInto(ivCripto,0,0,16)
                cripto.copyInto(valorCripto,0,16,cripto.size)
                val ivParams = IvParameterSpec(ivCripto)
                init(Cipher.DECRYPT_MODE,chave,ivParams)
                return String(doFinal(valorCripto))
            }
        } else return ""
    }

    override fun onLocationChanged(p0: Location) {
        binding.tvLocation.text = "${"%.4f".format(p0.latitude)}\n${"%.4f".format(p0.longitude)}"
    }
}

class Criptografador {

    val ks: KeyStore =
        KeyStore.getInstance("AndroidKeyStore").apply { load(null) }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getSecretKey(): SecretKey? {
        var chave: SecretKey? = null
        if(ks.containsAlias("chaveCripto")) {
            val entrada = ks.getEntry("chaveCripto", null) as?
                    KeyStore.SecretKeyEntry
            chave = entrada?.secretKey
        } else {
            val builder = KeyGenParameterSpec.Builder("chaveCripto",
                KeyProperties.PURPOSE_ENCRYPT or
                        KeyProperties.PURPOSE_DECRYPT)
            val keySpec = builder.setKeySize(256)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(
                    KeyProperties.ENCRYPTION_PADDING_PKCS7).build()
            val kg = KeyGenerator.getInstance("AES", "AndroidKeyStore")
            kg.init(keySpec)
            chave = kg.generateKey()
        }
        return chave
    }
}