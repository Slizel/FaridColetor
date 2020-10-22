package faridnet.com.faridcoletor

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import faridnet.com.faridcoletor.Data.AppDatabase
import faridnet.com.faridcoletor.Model.Produtos
import faridnet.com.faridcoletor.Model.ProgressDialog
import faridnet.com.faridcoletor.Viewmodel.AppViewModel
import kotlinx.android.synthetic.main.nomedoarquivo_dialog.view.*
import kotlinx.android.synthetic.main.password_dialog.view.*
import kotlinx.coroutines.launch
import java.io.*
import java.util.*
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {

    private lateinit var pAppViewModel: AppViewModel
    private lateinit var cAppViewModel: AppViewModel

    companion object {

        private const val PERMISSON_REQUEST_STORAGE = 1000
        private const val READ_REQUEST_CODE = 42
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pAppViewModel = ViewModelProvider(this).get(AppViewModel::class.java)
        cAppViewModel = ViewModelProvider(this).get(AppViewModel::class.java)

        //request permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSON_REQUEST_STORAGE
            )
        }

        setupActionBarWithNavController(findNavController(R.id.fragment))
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        // menu?.clear()
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_action -> {
                passwordInputExport()
            }
            R.id.add_action2 -> {
                performFileSearch()
                //openDirectory()
            }
            R.id.delete_all -> {
                clearDB()
            }
            R.id.delete_contagnesTable -> {
                clearContagens()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun passwordInputExport() {

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.password_dialog, null)

        val mBuilder =
            androidx.appcompat.app.AlertDialog.Builder(this).setCancelable(false)
                .setView(mDialogView)
                .setTitle("Exportar lista de contagens")
                .setMessage("Peça a senha para o responsável do balanço!")
        val mAlertDialog = mBuilder.show()

        mDialogView.dialogLoginBtn.setOnClickListener {

            val calander: Calendar = Calendar.getInstance()
            val dia = calander.get(Calendar.DAY_OF_MONTH)
            val mes = calander.get(Calendar.MONTH) + 1

            val senha = (dia + 20).toString() + (mes + 11).toString()

            val password = mDialogView.dialogPasswEt.text.toString()

            if (password == senha) {
                mAlertDialog.dismiss()


                export()

                Toast.makeText(this, "Compartilhamento iniciado!", Toast.LENGTH_SHORT)
                    .show()

            } else {
                Toast.makeText(this, "Senha Inválida!", Toast.LENGTH_SHORT).show()
                mAlertDialog.dismiss()
            }
        }
    }

    fun export() {

            Thread {

                val db = AppDatabase.getDatabase(this).contagensDao().allContagens

                runOnUiThread {
                    var filename: String
                    val mDialogView = LayoutInflater.from(this).inflate(R.layout.nomedoarquivo_dialog, null)
                    val mBuilder = AlertDialog.Builder(this).setCancelable(false)
                        .setView(mDialogView)
                        .setTitle("Digite o nome do arquivo")
                        .setMessage("Exemplo de nomeclatura: SecaoA, SecaoABC, SecaoAF. OBS: Não são aceitos caracteres especiais, numeros e espaço neste campo!")
                    val mAlertDialog = mBuilder.show()

                    mDialogView.confirmaNomeDoArquivoBtn.setOnClickListener {

                            if (mDialogView.nomeDoArquivo.text.toString() != "") {

                                try {

                                filename = mDialogView.nomeDoArquivo.text.toString()

                                val out: FileOutputStream = openFileOutput("$filename.txt", Context.MODE_PRIVATE)
                                out.write(db.toString().replace("[","")
                                    .replace("]","").replace(",","").replace(" ","").toByteArray())

                                out.close()

                                val context: Context = applicationContext
                                val filelocation = File(filesDir, "$filename.txt")
                                val path: Uri = FileProvider.getUriForFile(
                                    context,
                                    "faridnet.com.faridcoletor",
                                    filelocation
                                )
                                    mAlertDialog.dismiss()

                                val fileIntent = Intent(Intent.ACTION_SEND)
                                fileIntent.type = "text/txt"
                                fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Data")
                                fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                fileIntent.putExtra(Intent.EXTRA_STREAM, path)
                                startActivity(Intent.createChooser(fileIntent, "Send mail"))
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                            } else {
                                mAlertDialog.dismiss()
                            }
                    }
                }

        }.start()
    }

    private fun performFileSearch() {

        //Alert Dialog
        val builder = AlertDialog.Builder(this)
        builder.setPositiveButton("Sim") { _, _ ->

            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "text/*"
            startActivityForResult(intent, READ_REQUEST_CODE)

        }

        builder.setNegativeButton("Não") { _, _ ->
        }
        builder.setTitle("Importar Dados")
        builder.setMessage("Tem certeza que deseja importar os dados?")
        builder.create().show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == READ_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {

                val uri = data.data
                var path = uri!!.path
               // path = path!!.substring(path.indexOf(":") + 1)
               // Toast.makeText(this, "" + path, Toast.LENGTH_LONG).show()
                PopulateDB(uri)
            }
        }
    }

    private fun PopulateDB(uri: Uri): String? {

        var dialog = ProgressDialog.progressDialog(this)


        val text = StringBuilder()

                val inputStream = contentResolver.openInputStream(uri)
                val reader = BufferedReader(InputStreamReader(inputStream))


                try {

                    dialog.show()

                    //val br = BufferedReader(FileReader(file))
                    //var line: String?

                    //val stringBuilder = java.lang.StringBuilder()
                    var line: String?

                    var produtoId: String?
                    var codBarras: String?
                    var descricao: String?

                    while (reader.readLine().also { line = it } != null) {
                        dialog.show()

                        codBarras = line?.substring(0..15)
                        produtoId = line?.substring(16..23)
                        descricao = line?.substring(24..44)

                        var produto = Produtos(
                            codBarras.toString().trim(),
                            Integer.parseInt(produtoId.toString().trim()),
                            descricao.toString().trim()
                        )

                        pAppViewModel.addProdutos(produto)
                    }

                    Handler().postDelayed(
                        {
                            dialog.dismiss()
                        },
                        180000 // value in milliseconds
                    )

                    reader.close()

                } catch (e: IOException) {
                    e.printStackTrace()
                }

        return text.toString()
    }


    private fun clearDB() {

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.password_dialog, null)

        val mBuilder = AlertDialog.Builder(this).setCancelable(false)
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

                Toast.makeText(this, "Banco foi limpo", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this, "Senha Inválida!", Toast.LENGTH_SHORT).show()
                mAlertDialog.dismiss()
            }
        }
    }

    private fun clearContagens() {

        val mDialogView =
            LayoutInflater.from(this).inflate(R.layout.password_dialog, null)

        val mBuilder = AlertDialog.Builder(this).setCancelable(false)
            .setView(mDialogView)
            .setTitle("Limpar Lista de Contagens")
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

                Toast.makeText(this, "Banco foi limpo", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this, "Senha Inválida!", Toast.LENGTH_SHORT).show()
                mAlertDialog.dismiss()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        if (requestCode == PERMISSON_REQUEST_STORAGE) {
            Toast.makeText(this, "Permission granted!", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}






