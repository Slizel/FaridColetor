package faridnet.com.faridcoletor.Fragments.Add

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import faridnet.com.faridcoletor.Model.Contagens
import faridnet.com.faridcoletor.Model.Produtos
import faridnet.com.faridcoletor.R
import faridnet.com.faridcoletor.Viewmodel.AppViewModel
import kotlinx.android.synthetic.main.fragment_add.*
import kotlinx.android.synthetic.main.fragment_add.view.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddFragment : Fragment() {

    private lateinit var cAppViewModel: AppViewModel
    private lateinit var pAppViewModel: AppViewModel
   // private lateinit var produto: LiveData<Produtos>
    private val model: AppViewModel by viewModels()
   // private lateinit var prodId : TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Criar objeto da View Model
        pAppViewModel = ViewModelProvider(this).get(AppViewModel::class.java)
        cAppViewModel = ViewModelProvider(this).get(AppViewModel::class.java)
        //prodId.setText("")
        //produto = Produtos("", 0, "")

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        val viewTextDescricao = view.ViewTextDescricao
        val txtEdit = view.editTextTextCodBarras
        val txtEdit2 = view.editTextQuantidade

        txtEdit.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (txtEdit.text.toString() != "") {
                    lifecycleScope.launch {

                        val codBarras = editTextTextCodBarras.text.toString()
                        val produto = pAppViewModel.loadProdutobyCodBarra(codBarras)

                        if (produto != null) {
                            view.editTextQuantidade.setTextIsSelectable(true)
                            viewTextDescricao.text = produto.produtoId.toString() + " - " + produto.descricao
                            //viewTextDescricao.text = produto.value?.produtoId.toString() + " - " + produto.value?.descricao

                        } else {
                            view.editTextQuantidade.setTextIsSelectable(false)
                            viewTextDescricao.text = ""
                        }

                        //bgProduto(view, viewTextDescricao)
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                //viewTextDescricao.setText("")
                //viewTextDescricao.text = ""
            }
        })

        txtEdit2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

//            var handler: Handler = Handler(Looper.getMainLooper() /*UI thread*/)
//            var workRunnable: Runnable? = null

            override fun afterTextChanged(p0: Editable?) {

//                handler.removeCallbacks(workRunnable)
//                workRunnable = Runnable {
//
//                    txtEdit.requestFocus()
//                }
//                handler.postDelayed(workRunnable, 1200 /*delay*/)
            }
        })

        view.add_btn.setOnClickListener {
            insertDataToDatabase()
        }

        view.add_btn2.setOnClickListener {
            clearDatabase()
        }

        return view
    }

    // Database
    private fun insertDataToDatabase() {
        val codBarras = editTextTextCodBarras.text.toString()
        val qtde = editTextQuantidade.text.toString()
        var descricao = ViewTextDescricao.text.toString()
        var pos = ViewTextDescricao.text.indexOf("-")
        var input: CharSequence = ViewTextDescricao.getText().substring(0..pos -2)
        var prodId: String = input.toString()


        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())

        if (inputCheck(codBarras, qtde, descricao)) {
            //Create Product Object
            val contagem = Contagens(Integer.parseInt(prodId), qtde.toDouble(), currentDate.toString())

            // Add Data to Database
            cAppViewModel.addContagens(contagem)

            Toast.makeText(requireContext(), "Adicionado com sucesso", Toast.LENGTH_LONG).show()
            //findNavController().navigate(R.id.action_addFragment_to_listFragment)

        } else {
            Toast.makeText(requireContext(), "Preencha os campos, por gentileza", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun inputCheck(codBarras: String, qtde: String, descricao: String): Boolean {
        return !(TextUtils.isEmpty(codBarras) && TextUtils.isEmpty(qtde) && TextUtils.isEmpty(
            descricao
        ))
    }

    private fun clearDatabase() {
        //Alert Dialog
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Sim") { _, _ ->
            cAppViewModel.deleteAllContagens()
            pAppViewModel.deleteAllProdutos()

            Toast.makeText(
                requireContext(),
                "Banco foi limpo",
                Toast.LENGTH_SHORT
            ).show()
        }
        builder.setNegativeButton("Não") { _, _ -> }
        builder.setTitle("Limpar Banco de Dados")
        builder.setMessage("Tem certeza que deseja limpar o BD?")
        builder.create().show()
    }
}
//viewTextDescricao.text = produto.produtoId.toString() + " - " + produto.descricao