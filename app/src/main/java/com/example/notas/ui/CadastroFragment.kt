package com.example.notas.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.notas.databinding.FragmentCadastroBinding

class CadastroFragment : Fragment() {

    private var _binding: FragmentCadastroBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CadastroViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCadastroBinding.inflate(inflater, container, false)
        val view = binding.root
        viewModel = ViewModelProvider(this).get(CadastroViewModel::class.java)

        viewModel.status.observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(context, "Cadastro realizado com sucesso!", Toast.LENGTH_LONG).show()
                val direction = CadastroFragmentDirections.actionCadastroFragmentToHomeFragment()
                findNavController().navigate(direction)
            }
        }

        viewModel.msg.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }

        binding.btnCadastrarC.setOnClickListener {
            val email = binding.etEmailC.text.toString()
            val senha = binding.etSenhaC.text.toString()
            val repSenha = binding.etRepetSenhaC.text.toString()
            if (email != "" && senha != "" && repSenha != "") {
                if (senha == repSenha) {
                    viewModel.cadastrarUser(email, senha)
                }
                else {
                    Toast.makeText(context, "As senhas precisam ser iguais!", Toast.LENGTH_LONG).show()
                }
            }
            else {
                Toast.makeText(context, "Preencha todos os campos!", Toast.LENGTH_LONG).show()
            }
        }

        return view
    }
}