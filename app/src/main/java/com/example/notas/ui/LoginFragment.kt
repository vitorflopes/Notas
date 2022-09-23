package com.example.notas.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.notas.R
import com.example.notas.dao.AuthDao
import com.example.notas.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        viewModel.status.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigate(R.id.homeFragment)
            }
        }

        viewModel.msg.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }

        binding.btnEntrarL.setOnClickListener {
            val email = binding.etEmailL.text.toString()
            val senha = binding.etSenhaL.text.toString()

            if (email != "" && senha != "") {
                viewModel.autenticar(email, senha)
            }
            else {
                Toast.makeText(requireContext(), "Preencha todos os campos.", Toast.LENGTH_LONG).show()
            }
        }

        binding.btnCadastrarL.setOnClickListener {
            findNavController().navigate(R.id.cadastroFragment)
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        val currentUser = viewModel.retornaUsuarioLogado()
        if (currentUser != null) {
            val direction = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
            findNavController().navigate(direction)
        }
    }
}