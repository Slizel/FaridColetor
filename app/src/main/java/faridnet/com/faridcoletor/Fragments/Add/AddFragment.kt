package faridnet.com.faridcoletor.Fragments.Add

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import faridnet.com.faridcoletor.Model.Contagens
import faridnet.com.faridcoletor.R
import faridnet.com.faridcoletor.Viewmodel.AppViewModel
import kotlinx.android.synthetic.main.fragment_add.*
import kotlinx.android.synthetic.main.fragment_add.view.*
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*


class AddFragment : Fragment() {

    private lateinit var cAppViewModel: AppViewModel
    private lateinit var pAppViewModel: AppViewModel
    private lateinit var mAppViewModel: AppViewModel

    private var soundPool: SoundPool? = null
    private var sound1 = 0
    private var sound2 = 0
    private var sound3 = 0

    private val model: AppViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Criar objeto da View Model
        pAppViewModel = ViewModelProvider(this).get(AppViewModel::class.java)
        cAppViewModel = ViewModelProvider(this).get(AppViewModel::class.java)
        mAppViewModel = ViewModelProvider(this).get(AppViewModel::class.java)

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)


        soundPool = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            SoundPool.Builder()
                .setMaxStreams(3)
                .setAudioAttributes(audioAttributes)
                .build()
        } else {
            SoundPool(3, AudioManager.STREAM_MUSIC, 0)
        }

        sound1 = soundPool!!.load(requireContext(), R.raw.beep, 1)
        sound2 = soundPool!!.load(requireContext(), R.raw.triplebeep, 1)
        sound3 = soundPool!!.load(requireContext(), R.raw.successful, 1)

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        val viewTextDescricao = view.ViewTextDescricao

        val txtEdit = view.editTextTextCodBarras
        //txtEdit.requestFocus()
        val txtEdit2 = view.editTextQuantidade



        txtEdit.addTextChangedListener(object : TextWatcher {

            var handler: Handler = Handler(Looper.getMainLooper() /*UI thread*/)
            var workRunnable: Runnable? = null

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {


                if (txtEdit.text.toString() != "") {

                    lifecycleScope.launch {
                        val codBarras = editTextTextCodBarras.text.toString()
                        val produto = pAppViewModel.loadProdutobyCodBarra(codBarras)

                        if (produto != null) {

                            view.editTextQuantidade.setTextIsSelectable(true)
                            viewTextDescricao.text =
                                produto.produtoId.toString() + " - " + produto.descricao
                        } else {
                            view.editTextQuantidade.setTextIsSelectable(false)
                            viewTextDescricao.text = ""
                        }
                    }
                }
            }


            override fun afterTextChanged(p0: Editable?) {
                //Apaga o o TextView descrição qunado o valor do codBarras é inválido
                viewTextDescricao.text = ""
                lifecycleScope.launch {


                    handler.removeCallbacks(workRunnable)
                    workRunnable = Runnable {

                        txtEdit.requestFocus()
                    }
                    handler.postDelayed(workRunnable, 1200)

                    val prodId = editTextTextCodBarras.text.toString()
                    val contagens = cAppViewModel.loadContagens(prodId)

                    if (contagens != null) {
                        playTripleBeep()
                        // view.editTextQuantidade.setText(contagens.quantidade.toString())
                        view.editTextQuantidade.setTextIsSelectable(true)
                        ViewTextContagens.text = contagens.quantidade.toString()

                        editTextTextCodBarras.error =
                            "Já existe uma contagem gravada para este código"
                        editTextQuantidade.error =
                            "O que você digitar será somado a quantidade existente"

                        //Se o ViewTextContagem tem valor é porque já existe um Id cadastrado com uma qtde

                        txtEdit2.setOnKeyListener(object : View.OnKeyListener {
                            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                                // if the event is a key down event on the enter button
                                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {

                                    if (ViewTextContagens.text != "") {

                                        //Parametros do objeto Contagem para update
                                        val qtde = editTextQuantidade.text.toString()
                                        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                                        val currentDate = sdf.format(Date())

                                        //Edit text inicialmente é "", testar se foi colocado algum valor
                                        if (editTextQuantidade.text.toString() != "") {
                                            //Criar objeto
                                            val updateContagem = Contagens(
                                                prodId.toInt(),
                                                qtde.toDouble() + contagens.quantidade,
                                                currentDate
                                            )

                                            var soma = qtde.toDouble() + contagens.quantidade
                                            ViewTextContagens.text = soma.toString()

                                            //update DB
                                            mAppViewModel.updateContagens(updateContagem)
                                            successfulBeep()
                                            userVisibleHint = true

                                        }

                                        Toast.makeText(
                                            requireContext(),
                                            "Soma realizada!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }

                                    return true
                                }
                                return false
                            }
                        })


                    } else {
                        view.editTextQuantidade.setTextIsSelectable(false)
                        ViewTextContagens.text = ""
                        txtEdit.requestFocus()
                    }
                }
            }
        })





        view.add_btn.setOnClickListener {
            if (editTextTextCodBarras != null || editTextQuantidade != null) {
                insertDataToDatabase()
                playBeep()
                txtEdit2.setText("")
                txtEdit.setText("")
                txtEdit.requestFocus()
            }
        }

        setHasOptionsMenu(true)

        return view
    }

    fun playBeep() {
        soundPool!!.play(sound1, 1f, 1f, 0, 0, 1f)
    }

    fun playTripleBeep() {
        soundPool!!.play(sound2, 1f, 1f, 0, 0, 1f)
    }

    fun successfulBeep() {
        soundPool!!.play(sound3, 1f, 1f, 0, 0, 1f)
    }

    // Database
    private fun insertDataToDatabase() {
        val codBarras = editTextTextCodBarras.text.toString()
        val qtde = editTextQuantidade.text.toString()
        //var descricao = ViewTextDescricao.text.toString()

        if (inputCheck(codBarras, qtde)) {

            var pos = ViewTextDescricao.text.indexOf("-")
            if (pos > 0) {
                var input: CharSequence = ViewTextDescricao.getText().substring(0..pos - 2)
                var prodId: String = input.toString()

                val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                val currentDate = sdf.format(Date())

                //Create Product Object
                val contagem =
                    Contagens(Integer.parseInt(prodId), qtde.toDouble(), currentDate.toString())

                // Add Data to Database
                cAppViewModel.addContagens(contagem)

                Toast.makeText(requireContext(), "Adicionado com sucesso", Toast.LENGTH_LONG).show()
                //findNavController().navigate(R.id.action_addFragment_to_listFragment)

            } else {

                editTextTextCodBarras.error =
                    "Código não existe no arquivo"

                editTextTextCodBarras.setText("")

            }

        } else {
            Toast.makeText(requireContext(), "Preencha os campos, por gentileza", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun inputCheck(codBarras: String, qtde: String): Boolean {
        return !(TextUtils.isEmpty(codBarras) && TextUtils.isEmpty(qtde) || TextUtils.isEmpty(
            codBarras
        ) || TextUtils.isEmpty(qtde))
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear();
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.delete_all) {
            clearDatabase()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        editTextQuantidade.setText("")
        editTextTextCodBarras.setText("")
        if (isVisibleToUser) {
            if (getFragmentManager() != null) {
                getFragmentManager()
                    ?.beginTransaction()
                    ?.detach(this)
                    ?.attach(this)
                    ?.commit();
            }
        }
    }

}
