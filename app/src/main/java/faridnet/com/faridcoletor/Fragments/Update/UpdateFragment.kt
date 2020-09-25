package faridnet.com.faridcoletor.Fragments.Update

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import faridnet.com.faridcoletor.Model.Contagens
import faridnet.com.faridcoletor.R
import faridnet.com.faridcoletor.Viewmodel.AppViewModel
import kotlinx.android.synthetic.main.fragment_update.*
import kotlinx.android.synthetic.main.fragment_update.view.*
import java.text.SimpleDateFormat
import java.util.*


class UpdateFragment : Fragment() {

    private val args by navArgs<UpdateFragmentArgs>()

    private lateinit var cAppViewModel: AppViewModel
    private lateinit var mAppViewModel: AppViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_update, container, false)

        //cAppViewModel = ViewModelProvider(this).get(AppViewModel::class.java)
        mAppViewModel = ViewModelProvider(this).get(AppViewModel::class.java)

        view.updateQuantidade.setText(args.currentContagem.quantidade.toString())
        //view.updateQuantidade.setText(args.currentJoinContagensProduto.contagemQuantidade.toString())

        view.update_btn.setOnClickListener {
            updateDb()
        }

        return view
    }

    private fun updateDb() {

        val qtde = updateQuantidade.text.toString()

        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())

        if (inputCheck(qtde)) {
            //Criar objeto
            val updateContagem = Contagens(
                args.currentContagem.produtoId,
                qtde.toDouble(), currentDate
            )

            cAppViewModel.updateContagens(updateContagem)

            Toast.makeText(requireContext(), "Atualizado com sucesso!", Toast.LENGTH_SHORT).show()

            //Navigate back
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)

        } else {
            Toast.makeText(
                requireContext(),
                "Gentileza preencher todos os campos.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun inputCheck(qtde: String): Boolean {
        return !(TextUtils.isEmpty(qtde) && TextUtils.isEmpty(
            qtde
        ))
    }
}