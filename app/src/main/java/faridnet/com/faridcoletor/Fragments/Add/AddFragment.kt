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
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import faridnet.com.faridcoletor.Model.Contagens
import faridnet.com.faridcoletor.R
import faridnet.com.faridcoletor.Viewmodel.AppViewModel
import kotlinx.android.synthetic.main.fragment_add.*
import kotlinx.android.synthetic.main.fragment_add.view.*
import kotlinx.android.synthetic.main.password_dialog.view.*
import kotlinx.coroutines.launch
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
    private var sound4 = 0

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
                .setMaxStreams(4)
                .setAudioAttributes(audioAttributes)
                .build()
        } else {
            SoundPool(4, AudioManager.STREAM_MUSIC, 0)
        }

        sound1 = soundPool!!.load(requireContext(), R.raw.beep, 1)
        sound2 = soundPool!!.load(requireContext(), R.raw.triplebeep, 1)
        sound3 = soundPool!!.load(requireContext(), R.raw.successful, 1)
        sound4 = soundPool!!.load(requireContext(), R.raw.wrong, 1)

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        val viewTextDescricao = view.ViewTextDescricao
        val ViewTextContagens = view.ViewTextContagens
        val txtEditCodBarras = view.editTextTextCodBarras
        val txtEditQuantidade = view.editTextQuantidade

        //Altera a exibição do do imput type do edit text para mostrar numeros ao inves de ****
        txtEditCodBarras.transformationMethod = null
        txtEditQuantidade.transformationMethod = null

        txtEditCodBarras.addTextChangedListener(object : TextWatcher {

            var handler: Handler = Handler(Looper.getMainLooper() /*UI thread*/)
            var workRunnable: Runnable? = null

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {


                if (txtEditCodBarras.text.toString().length == 13) {

                    txtEditQuantidade.requestFocus()
                }

                handler.removeCallbacks(workRunnable)
                workRunnable = Runnable {

                    if (txtEditCodBarras.text.toString().length == 8) {


                    } else if (txtEditCodBarras.text.toString().length == 7) {

                        txtEditQuantidade.requestFocus()

                    } else if (txtEditCodBarras.text.toString().length == 6) {

                        txtEditQuantidade.requestFocus()

                    } else if (txtEditCodBarras.text.toString().length == 4) {

                        txtEditQuantidade.requestFocus()

                    } else if (txtEditCodBarras.text.toString().length == 3) {

                        txtEditQuantidade.requestFocus()

                    } else if (txtEditCodBarras.text.toString().length == 2) {

                        txtEditQuantidade.requestFocus()

                    } else if (txtEditCodBarras.text.toString().length == 1) {

                        txtEditQuantidade.requestFocus()
                    }
                }
                handler.postDelayed(workRunnable, 1200)

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // TODO Auto-generated method stub
            }

            override fun afterTextChanged(s: Editable) {
                // TODO Auto-generated method stub
            }
        })

        txtEditCodBarras.setOnFocusChangeListener { _, hasFocus ->

            if (!hasFocus && txtEditCodBarras.text.toString() != "") {

                lifecycleScope.launch {
                    val codBarras = editTextTextCodBarras.text.toString()
                    ConsultaCodBarras(codBarras)
                }
            } else {
                viewTextDescricao.text = ""
                ViewTextContagens.text = ""
                txtEditCodBarras.setText("")
                txtEditQuantidade.setText("")

                txtEditCodBarras.requestFocus()
            }
        }

        AdicionarOuSomarActionTeclado(txtEditCodBarras, view, viewTextDescricao, ViewTextContagens, txtEditQuantidade)

        view.add_btn.setOnClickListener() {
            btnAdd()
        }

        setHasOptionsMenu(true)

        return view
    }

    private fun AdicionarOuSomarActionTeclado(txtEditCodBarras: EditText, view: View, viewTextDescricao: TextView, ViewTextContagens: TextView, txtEditQuantidade: EditText) {

        txtEditCodBarras.setOnFocusChangeListener { _, hasFocus ->

            if (!hasFocus) {
                if (txtEditCodBarras.text.toString() != "") {

                    lifecycleScope.launch {
                        val codBarras = editTextTextCodBarras.text.toString()
                        val produto = pAppViewModel.loadProdutobyCodBarra(codBarras)

                        if (produto != null) {
                            view.editTextQuantidade.setTextIsSelectable(true)
                            viewTextDescricao.text =
                                produto.produtoId.toString() + " - " + produto.descricao

                                editTextQuantidade.setOnKeyListener(object : View.OnKeyListener {override fun onKey(v: View?,keyCode: Int, event: KeyEvent): Boolean {
                                        if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                                             if(editTextQuantidade.text.toString() != ""){
                                            insertDataToDatabase()
                                            }

                                            return true
                                        }
                                    return false
                                } })


                            val contagens = cAppViewModel.loadContagens(produto.produtoId.toString())

                            if (contagens != null) {
                                ViewTextContagens.text = contagens.quantidade.toString()
                                playTripleBeep()

                                editTextTextCodBarras.error = "Já existe uma contagem gravada para este código"
                                editTextQuantidade.error = "O que você digitar será somado a quantidade existente"

                                //Se o ViewTextContagem tem valor é porque já existe um Id cadastrado com uma qtde
                                txtEditQuantidade.setOnKeyListener(object : View.OnKeyListener { override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
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
                                                        produto.produtoId,
                                                        qtde.toDouble() + contagens.quantidade,
                                                        currentDate
                                                    )

                                                    var soma =
                                                        qtde.toDouble() + contagens.quantidade
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
                                //view.editTextQuantidade.setTextIsSelectable(false)
                                ViewTextContagens.text = ""
                                //txtEdit.requestFocus()
                            }

                        } else {
                            view.editTextQuantidade.setTextIsSelectable(false)
                            viewTextDescricao.text = ""

                            //view.editTextTextCodBarras.requestFocus()
                            editTextTextCodBarras.error =
                                "Produto não encontrado"
                            wrongBeep()
                            Toast.makeText(
                                requireContext(),
                                "Produto não encontrado",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun btnAdd() {
        if (editTextTextCodBarras != null && editTextQuantidade.text != null && ViewTextContagens.text.isEmpty()) {
            insertDataToDatabase()

        } else if (editTextTextCodBarras != null && editTextQuantidade != null && !ViewTextContagens.text.isEmpty()) {
            Soma()
        }
    }

    private fun ConsultaCodBarras(codBarras: String) {
        lifecycleScope.launch {

            val produto = pAppViewModel.loadProdutobyCodBarra(codBarras)

            if (produto != null) {

                ViewTextDescricao.text = produto.produtoId.toString() + " - " + produto.descricao
                val contagen = cAppViewModel.loadContagens(produto.produtoId.toString())
                //beep produto encontrado

                if (contagen != null) {
                    playTripleBeep()
                    ViewTextContagens.text = contagen.quantidade.toString()
                    editTextTextCodBarras.error = "Já existe uma contagem gravada para este código"
                    editTextQuantidade.error =
                        "O que você digitar será somado a quantidade existente"
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Código não encontrado!",
                    Toast.LENGTH_LONG
                ).show()
                wrongBeep()
            }
        }
    }

    private fun Soma() {

                    lifecycleScope.launch {
                        val produto =
                            pAppViewModel.loadProdutobyCodBarra(editTextTextCodBarras.text.toString())

                        if (produto != null) {
                            view?.ViewTextDescricao?.text
                                ?: produto.produtoId.toString() + " - " + produto.descricao
                            val contagem = cAppViewModel.loadContagens(produto.produtoId.toString())
                            if (contagem != null) {

                                val qtde = editTextQuantidade.text.toString()
                                val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                                val currentDate = sdf.format(Date())

                                if (editTextQuantidade.text.toString() != "") {
                                    // Criar objeto
                                    val updateContagem = Contagens(
                                        produto.produtoId,
                                        qtde.toDouble() + contagem.quantidade,
                                        currentDate
                                    )
                                    //update DB
                                    mAppViewModel.updateContagens(updateContagem)
                                    successfulBeep()
                                    userVisibleHint = true

                                    Toast.makeText(
                                        requireContext(),
                                        "Soma realizada!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    }
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
                val contagem = Contagens(
                    Integer.parseInt(prodId),
                    qtde.toDouble(),
                    currentDate.toString()
                )

                // Add Data to Database
                cAppViewModel.addContagens(contagem)
                playBeep()
                Toast.makeText(requireContext(), "Adicionado com sucesso", Toast.LENGTH_LONG).show()
                userVisibleHint = true
            } else {
                wrongBeep()
                Toast.makeText(requireContext(), "Código não existe no arquivo", Toast.LENGTH_LONG)
                    .show()
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


    private fun clearDB() {

        val mDialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.password_dialog, null)

        val mBuilder = AlertDialog.Builder(requireContext()).setCancelable(false)
            .setView(mDialogView)
            .setTitle("Limpar Banco de Dados")
            .setMessage("Peça a senha para o responsável do balanço. Essa ação não é reversível!")
        val mAlertDialog = mBuilder.show()

        mDialogView.dialogLoginBtn.setOnClickListener {

            val calander: Calendar = Calendar.getInstance()
            var dia = calander.get(Calendar.DAY_OF_MONTH)
            var mes = calander.get(Calendar.MONTH) + 1

            val senha = (dia + 20).toString() + (mes + 11).toString()

            val password = mDialogView.dialogPasswEt.text.toString()

            if (password == senha) {
                mAlertDialog.dismiss()

                cAppViewModel.deleteAllContagens()
                pAppViewModel.deleteAllProdutos()

                Toast.makeText(requireContext(), "Banco foi limpo", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(requireContext(), "Senha Inválida!", Toast.LENGTH_SHORT).show()
                mAlertDialog.dismiss()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.delete_all) {
            clearDB()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun LimpaCampos() {
        val viewTextDescricao = view?.ViewTextDescricao
        val ViewTextContagens = view?.ViewTextContagens
        val txtEditCodBarras = view?.editTextTextCodBarras
        val txtEditQuantidade = view?.editTextQuantidade

        viewTextDescricao?.text = ""
        ViewTextContagens?.text = ""
        txtEditCodBarras?.setText("")
        txtEditQuantidade?.setText("")
        txtEditCodBarras?.requestFocus()
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

    fun playBeep() {
        soundPool!!.play(sound1, 1f, 1f, 0, 0, 1f)
    }

    fun playTripleBeep() {
        soundPool!!.play(sound2, 1f, 1f, 0, 0, 1f)
    }

    fun successfulBeep() {
        soundPool!!.play(sound3, 1f, 1f, 0, 0, 1f)
    }

    fun wrongBeep() {
        soundPool!!.play(sound4, 1f, 1f, 0, 0, 1f)
    }
}



