package faridnet.com.faridcoletor.Fragments.Add

import android.annotation.SuppressLint
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
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
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.zxing.integration.android.IntentIntegrator
import faridnet.com.faridcoletor.Model.Contagens
import faridnet.com.faridcoletor.R
import faridnet.com.faridcoletor.Viewmodel.AppViewModel
import kotlinx.android.synthetic.main.fragment_add.*
import kotlinx.android.synthetic.main.fragment_add.view.*
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class AddFragment : Fragment() {

    private lateinit var cAppViewModel: AppViewModel
    private lateinit var pAppViewModel: AppViewModel
    private lateinit var mAppViewModel: AppViewModel

    private var soundPool: SoundPool? = null
    private var sound1 = 0
    private var sound2 = 0
    private var sound3 = 0
    private var sound4 = 0
    private var sound5 = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {

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
                .setMaxStreams(5)
                .setAudioAttributes(audioAttributes)
                .build()

        } else {

            SoundPool(5, AudioManager.STREAM_MUSIC, 0)

        }
        sound1 = soundPool!!.load(requireContext(), R.raw.beep, 1)
        sound2 = soundPool!!.load(requireContext(), R.raw.triplebeep, 1)
        sound3 = soundPool!!.load(requireContext(), R.raw.successful, 1)
        sound4 = soundPool!!.load(requireContext(), R.raw.wrong, 1)
        sound5 = soundPool!!.load(requireContext(), R.raw.found, 2)

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        val df = DecimalFormat("######,###000000.000")

        val ImageButton = view.scan_buttom
        val viewTextDescricao = view.ViewTextDescricao
        val ViewTextContagens = view.ViewTextContagens
        val txtEditCodBarras = view.editTextTextCodBarras
        val txtEditQuantidade = view.editTextQuantidade

       // txtEditCodBarras.requestFocus()
       // txtEditCodBarras?.showKeyboard()

        //Altera a exibição do do imput type do edit text para mostrar numeros ao inves de ****
        txtEditCodBarras.transformationMethod = null
        txtEditQuantidade.transformationMethod = null


        txtEditCodBarras.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if (txtEditCodBarras.text.toString() == "") {

                    viewTextDescricao.text = ""
                    ViewTextContagens.text = ""
                    txtEditQuantidade.setText("")
                    txtEditQuantidade.error = null

                }


            }

            override fun afterTextChanged(p0: Editable?) {

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

        ImageButton.setOnClickListener(View.OnClickListener {
            scanFromFragment()
        })

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

    fun wrongBeep() {
        soundPool!!.play(sound4, 1f, 1f, 0, 0, 1f)
    }

    fun foundBeep() {
        soundPool!!.play(sound5, 1f, 1f, 0, 0, 1f)
    }

    fun scanFromFragment() {
        IntentIntegrator.forSupportFragment(this).initiateScan()
    }

    //Para usar o botão de Scan
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (intentResult != null) {
            if (intentResult.contents != null) {
                alert("Scan realizado")
                editTextTextCodBarras.setText(intentResult.contents)

            } else {
                alert("Scan cancelado")
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun alert(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
    }

    private fun AdicionarOuSomarActionTeclado(txtEditCodBarras: EditText, view: View, viewTextDescricao: TextView, ViewTextContagens: TextView, txtEditQuantidade: EditText) {

        txtEditCodBarras.setOnFocusChangeListener { _, hasFocus ->

            if (!hasFocus) {
                if (txtEditCodBarras.text.toString() != "") {

                    lifecycleScope.launch {
                        val codBarras = editTextTextCodBarras.text.toString()
                        val produto = pAppViewModel.loadProdutobyCodBarra(codBarras)


                        if (produto != null) {
                            foundBeep()
                            viewTextDescricao.text = produto.produtoId.toString() + " - " + produto.descricao

                                editTextQuantidade.setOnKeyListener(object : View.OnKeyListener {
                                    override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                                        if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                                            if (editTextQuantidade.text.toString() != "") {
                                                insertDataToDatabase()
                                                LimpaCampos()
                                                HandleRequestFocus()
                                            }
                                            return true
                                        }
                                        return false
                                    }
                                })

                            val contagens = cAppViewModel.loadContagens(produto.produtoId.toString())

                            // Se o produto já foi contado uma vez

                            if (contagens != null ) {

                                    ViewTextContagens.text = contagens.quantidade.toString()

                                //  playTripleBeep()
                                editTextTextCodBarras.error = "Já existe uma contagem gravada para este código"
                                editTextQuantidade.error = "O que você digitar será somado a quantidade existente"

                                txtEditQuantidade.setOnKeyListener(object : View.OnKeyListener {

                                    @SuppressLint("SimpleDateFormat")
                                    override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                                        if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                                            
                                            if (ViewTextContagens.text != "") {

                                                //Parametros do objeto Contagem para update
                                                val qtde = editTextQuantidade.text.toString()
                                                val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                                                val currentDate = sdf.format(Date())

                                                //Edit text inicialmente é "", testar se foi colocado algum valor
                                                if (editTextQuantidade.text.toString() != "") {

                                                    //Criar objeto
                                                    val updateContagem = Contagens(produto.produtoId,
                                                        qtde.toDouble() + contagens.quantidade,
                                                        currentDate)

                                                    val soma = qtde.toDouble() + contagens.quantidade
                                                    ViewTextContagens.text = soma.toString()

                                                    if (qtde.toDouble() <= 999999.000) {

                                                        //Update DB
                                                        mAppViewModel.updateContagens(updateContagem)
                                                        successfulBeep()
                                                        Toast.makeText(requireContext(), "Soma realizada!", Toast.LENGTH_SHORT).show()
                                                        LimpaCampos()
                                                        HandleRequestFocus()

                                                    } else {

                                                        Toast.makeText(requireContext(), "A quantidade não pode ser maior que 999999.000", Toast.LENGTH_SHORT).show()
                                                        wrongBeep()
                                                        LimpaQuantidade()
                                                        viewTextDescricao.text = ""
                                                        ViewTextContagens.text = ""
                                                        txtEditCodBarras.setText("")
                                                        HandleRequestFocus()

                                                    }
                                                }
                                            }
                                            return true
                                        }
                                        return false
                                    }
                                })

                            } else {

                                ViewTextContagens.text = ""
                                editTextTextCodBarras.error = null
                                editTextQuantidade.error = null

                            }

                        } else {

                            viewTextDescricao.text = ""
                            editTextQuantidade.error = null
                            editTextQuantidade.error = null
                            wrongBeep()
                            editTextTextCodBarras.error = "Produto não encontrado"
                            LimpaCampos()
                            CodBarrarsFocus()
                            //userVisibleHint = true
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
                val contagem = cAppViewModel.loadContagens(produto.produtoId.toString())
                //beep produto encontrado

                if (contagem != null) {
                    playTripleBeep()

                    val qtde = editTextQuantidade.text.toString()
                    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                    val currentDate = sdf.format(Date())
                        // Criar objeto
                        val updateContagem = Contagens(
                            produto.produtoId, qtde.toDouble() + contagem.quantidade, currentDate)

                    if (qtde.toDouble() <= 999999.000) {
                        ViewTextContagens.text = contagem.quantidade.toString()
                    }

                    editTextTextCodBarras.error = "Já existe uma contagem gravada para este código"
                    editTextQuantidade.error = "O que você digitar será somado a quantidade existente"
                }

            } else {

                Toast.makeText(requireContext(), "Código não encontrado!", Toast.LENGTH_LONG).show()
                wrongBeep()
                //LimpaCampos()
                userVisibleHint = true
            }
        }
    }

    private fun Soma() {
                    lifecycleScope.launch {
                        val produto =
                            pAppViewModel.loadProdutobyCodBarra(editTextTextCodBarras.text.toString())

                        if (produto != null) {
                            view?.ViewTextDescricao?.text ?: produto.produtoId.toString() + " - " + produto.descricao
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

                                    if (qtde.toDouble() <= 999999.000) {

                                        //update DB
                                        mAppViewModel.updateContagens(updateContagem)
                                        successfulBeep()
                                        Toast.makeText(requireContext(), "Soma realizada!", Toast.LENGTH_LONG).show()
                                        //userVisibleHint = true
                                        LimpaCampos()
                                        CodBarrarsFocus()
                                        editTextTextCodBarras.error = null

                                    } else {

                                        Toast.makeText(requireContext(), "A quantidade não pode ser maior que 999999.000", Toast.LENGTH_SHORT).show()
                                        wrongBeep()
                                        LimpaQuantidade()
                                    }

                                }
                            }
                        }
                    }
    }

    // Database
    @SuppressLint("SimpleDateFormat")
    private fun insertDataToDatabase() {
        val codBarras = editTextTextCodBarras.text.toString()
        val qtde = editTextQuantidade.text.toString()
        //var descricao = ViewTextDescricao.text.toString()

        if (inputCheck(codBarras, qtde)) {
            var pos = ViewTextDescricao.text.indexOf("-")
            if (pos > 0) {
                val input: CharSequence = ViewTextDescricao.text.substring(0..pos - 2)
                val prodId: String = input.toString()

                val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                val currentDate = sdf.format(Date())

                //Create Product Object
                val contagem = Contagens(Integer.parseInt(prodId), qtde.toDouble(), currentDate.toString())


                if (qtde.toDouble() <= 999999.000) {

                    // Add Data to Database
                    cAppViewModel.addContagens(contagem)
                    playBeep()
                    Toast.makeText(requireContext(), "Atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                    //userVisibleHint = true
                    LimpaCampos()
                    CodBarrarsFocus()

                } else {

                    Toast.makeText(requireContext(), "A quantidade não pode ser maior que 999999.000", Toast.LENGTH_SHORT).show()
                    wrongBeep()
                    LimpaQuantidade()
                   // LimpaCampos()
                }

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
        return !(TextUtils.isEmpty(codBarras) && TextUtils.isEmpty(qtde) || TextUtils.isEmpty(codBarras) || TextUtils.isEmpty(qtde))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) { menu.clear()
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.delete_all).isVisible = false
        menu.findItem(R.id.add_action).isVisible = false
        menu.findItem(R.id.add_action2).isVisible = false
        menu.findItem(R.id.delete_contagnesTable).isVisible = false
        super.onPrepareOptionsMenu(menu)
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
    }

    private fun CodBarrarsFocus(){

        val txtEditCodBarras = view?.editTextTextCodBarras
        txtEditCodBarras?.requestFocus()
    }

    private fun LimpaQuantidade(){

        val txtEditQuantidade = view?.editTextQuantidade
        txtEditQuantidade?.setText("")

    }

    private fun HandleRequestFocus (){

        var handler: Handler = Handler(Looper.getMainLooper() /*UI thread*/)
        var workRunnable: Runnable? = null

        handler.removeCallbacks(workRunnable)
        workRunnable = Runnable {

            CodBarrarsFocus()
            editTextTextCodBarras.error = null
        }
        handler.postDelayed(workRunnable, 25)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        if (isVisibleToUser) {
            if (fragmentManager != null) {

                fragmentManager
                    ?.beginTransaction()
                    ?.detach(this)
                    ?.attach(this)
                    ?.commit();

                CodBarrarsFocus()
            }
            CodBarrarsFocus()
        }

        LimpaCampos()
        CodBarrarsFocus()

    }

//    fun View.showKeyboard() {
//        this.requestFocus()
//        val inputMethodManager = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
//        inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
//    }

//    fun View.hideKeyboard() {
//        val inputMethodManager = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
//        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
//    }

}





