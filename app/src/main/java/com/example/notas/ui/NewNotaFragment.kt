package com.example.notas.ui

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.notas.R
import com.example.notas.databinding.FragmentHomeBinding
import com.example.notas.databinding.FragmentNewNotaBinding

class NewNotaFragment : Fragment() {

    private var _binding: FragmentNewNotaBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: NewNotaViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewNotaBinding.inflate(inflater, container, false)
        val view = binding.root
        viewModel = ViewModelProvider(this).get(NewNotaViewModel::class.java)

        //val captureImageIntent = takePhoto.contract.createIntent(requireContext(), Uri.EMPTY)

        return view
    }
}