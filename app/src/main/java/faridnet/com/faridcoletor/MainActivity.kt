package faridnet.com.faridcoletor

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import faridnet.com.faridcoletor.Data.AppDatabase
import faridnet.com.faridcoletor.Model.Produtos
import faridnet.com.faridcoletor.Model.ProgressDialog
import faridnet.com.faridcoletor.Viewmodel.AppViewModel
import kotlinx.android.synthetic.main.fragment_add.*
import java.io.*

class MainActivity : AppCompatActivity() {

    private lateinit var pAppViewModel: AppViewModel


    companion object {

        private val PERMISSON_REQUEST_STORAGE = 1000
        private val READ_REQUEST_CODE = 42
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pAppViewModel = ViewModelProvider(this).get(AppViewModel::class.java)

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
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId;
        if (id == R.id.add_action) {
            export()

        } else if (id == R.id.add_action2) {

            var dialog = ProgressDialog.progressDialog(this)
            dialog.show()
            performFileSearch()

            Handler().postDelayed(
                {
                    dialog.dismiss()
                },
                180000 // value in milliseconds
            )

        }

        return super.onOptionsItemSelected(item)
    }

    fun export() {

        Thread {

            val db = AppDatabase.getDatabase(this).contagensDao().allContagens

            try {
                //saving the file into device
                val out: FileOutputStream = openFileOutput("data.csv", Context.MODE_PRIVATE)
                out.write(db.toString().toByteArray())
                out.close()

                //exporting
                val context: Context = applicationContext
                val filelocation = File(filesDir, "data.csv")
                val path: Uri = FileProvider.getUriForFile(
                    context,
                    "faridnet.com.faridcoletor",
                    filelocation
                )
                val fileIntent = Intent(Intent.ACTION_SEND)
                fileIntent.type = "text/csv"
                fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Data")
                fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                fileIntent.putExtra(Intent.EXTRA_STREAM, path)
                startActivity(Intent.createChooser(fileIntent, "Send mail"))
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }.start()
    }

    private fun PopulateDB(input: String): String? {

        val file = File(input)
        val text = StringBuilder()

        try {
            val br = BufferedReader(FileReader(file))
            var line: String?

            var produtoId: String?
            var codBarras: String?
            var descricao: String?

            while (br.readLine().also { line = it } != null) {

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
            br.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return text.toString()
    }

    private fun performFileSearch() {

        //Alert Dialog
        val builder = AlertDialog.Builder(this)
        builder.setPositiveButton("Sim") { _, _ ->

            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "text/*"
            startActivityForResult(intent, READ_REQUEST_CODE)
        }
        builder.setNegativeButton("Não") { _, _ -> }
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
                path = path!!.substring(path.indexOf(":") + 1)
                Toast.makeText(this, "" + path, Toast.LENGTH_LONG).show()
                PopulateDB(path)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSON_REQUEST_STORAGE) {
            Toast.makeText(this, "Permission granted!", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "PErmission not granted", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}





