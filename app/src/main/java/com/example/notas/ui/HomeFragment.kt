package com.example.notas.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.notas.R
import com.example.notas.databinding.FragmentCadastroBinding
import com.example.notas.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        binding.btnAddNotaH.setOnClickListener {
            findNavController().navigate(R.id.newNotaFragment)
        }

        binding.btnSairH.setOnClickListener {
            viewModel.deslogar()
            val direction = HomeFragmentDirections.actionHomeFragmentToLoginFragment()
            findNavController().navigate(direction)
        }

        return view
    }
}