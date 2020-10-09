package faridnet.com.faridcoletor.Fragments.Update

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import faridnet.com.faridcoletor.Model.Contagens
import faridnet.com.faridcoletor.R
import faridnet.com.faridcoletor.Viewmodel.AppViewModel
import kotlinx.android.synthetic.main.fragment_update.*
import kotlinx.android.synthetic.main.fragment_update.view.*
import kotlinx.android.synthetic.main.password_dialog.view.*
import java.text.SimpleDateFormat
import java.util.*


class UpdateFragment : Fragment() {

    private val args by navArgs<UpdateFragmentArgs>()

    private lateinit var mAppViewModel: AppViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_update, container, false)

        //cAppViewModel = ViewModelProvider(this).get(AppViewModel::class.java)
        mAppViewModel = ViewModelProvider(this).get(AppViewModel::class.java)

        view.updateQuantidade.setText(args.currentJoin.contagemQuantidade.toString())
        //view.updateQuantidade.setText(args.currentJoinContagensProduto.contagemQuantidade.toString())

        view.update_btn.setOnClickListener {
            updateDb()
        }

        view.delete_btn.setOnClickListener {
            deletarItem()
        }

        setHasOptionsMenu(true)

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.delete_all).isVisible = false
        menu.findItem(R.id.add_action).isVisible = false
        menu.findItem(R.id.add_action2).isVisible = false
        menu.findItem(R.id.delete_contagnesTable).isVisible = false
        super.onPrepareOptionsMenu(menu)
    }

    @SuppressLint("SimpleDateFormat")
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

    private fun deletarItem() {

        val mDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.password_dialog, null)

        val mBuilder =
            androidx.appcompat.app.AlertDialog.Builder(requireContext()).setCancelable(false)
                .setView(mDialogView)
                .setTitle("Deletar Item")
                .setMessage("Peça a senha para o responsável do balanço. Essa ação não é reversível!")
        val mAlertDialog = mBuilder.show()

        mDialogView.dialogLoginBtn.setOnClickListener {

            val calander: Calendar = Calendar.getInstance()
            val dia = calander.get(Calendar.DAY_OF_MONTH)
            val mes = calander.get(Calendar.MONTH) + 1

            val senha = (dia + 20).toString() + (mes + 11).toString()

            val password = mDialogView.dialogPasswEt.text.toString()

            if (password == senha) {
                mAlertDialog.dismiss()

                val deleteContagem = Contagens(
                    args.currentJoin.produtoId,
                    args.currentJoin.contagemQuantidade.toDouble(), ""
                )

                mAppViewModel.deleteContagens(deleteContagem)

                Toast.makeText(requireContext(), "Item deletado com sucesso!", Toast.LENGTH_SHORT)
                    .show()

                findNavController().navigate(R.id.action_updateFragment_to_listFragment)


            } else {
                Toast.makeText(requireContext(), "Senha Inválida!", Toast.LENGTH_SHORT).show()
                mAlertDialog.dismiss()
            }
        }
    }
}