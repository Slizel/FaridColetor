package faridnet.com.faridcoletor.Fragments.Update

import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.room.RoomDatabase
import faridnet.com.faridcoletor.Model.Contagens
import faridnet.com.faridcoletor.R
import faridnet.com.faridcoletor.Viewmodel.AppViewModel
import kotlinx.android.synthetic.main.fragment_update.*
import kotlinx.android.synthetic.main.fragment_update.view.*
import java.text.SimpleDateFormat
import java.util.*


class UpdateFragment : Fragment() {

    private val args by navArgs<UpdateFragmentArgs>()

    private lateinit var mAppViewModel: AppViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_update, container, false)

        //cAppViewModel = ViewModelProvider(this).get(AppViewModel::class.java)
        mAppViewModel = ViewModelProvider(this).get(AppViewModel::class.java)

        view.updateQuantidade.setText(args.currentJoin.contagemQuantidade.toString())
        //view.updateQuantidade.setText(args.currentJoinContagensProduto.contagemQuantidade.toString())

        view.update_btn.setOnClickListener {
            updateDb()
        }

        view.delete_btn.setOnClickListener{
            deleteJoinContagem()
        }

        setHasOptionsMenu(true)

        return view
    }

    private fun updateDb() {

        val qtde = updateQuantidade.text.toString()
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())

        if (inputCheck(qtde)) {
            //Criar objeto
            val updateContagem = Contagens(
                args.currentJoin.produtoId,
                qtde.toDouble(), currentDate
            )

            mAppViewModel.updateContagens(updateContagem)

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

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.main_menu, menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if(item.itemId == R.id.delete_action){
//            deleteJoinContagem()
//        }
//        return super.onOptionsItemSelected(item)
//    }

    private fun deleteJoinContagem() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Sim") { _, _ ->

            val deleteContagem = Contagens(args.currentJoin.produtoId,
            args.currentJoin.contagemQuantidade.toDouble(), "")

            mAppViewModel.deleteContagens(deleteContagem)

            Toast.makeText(requireContext(), "Deletado com sucesso!", Toast.LENGTH_SHORT).show()

            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }

        builder.setNegativeButton("Não") { _, _ -> }
        builder.setTitle("Deletar?")
        builder.setMessage("Você tem certeza de que irá deletar?")
        builder.create().show()
    }
}