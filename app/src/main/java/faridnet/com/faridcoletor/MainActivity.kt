package faridnet.com.faridcoletor

import android.content.Context
import android.content.Intent
import android.net.Uri

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import faridnet.com.faridcoletor.Data.AppDatabase
import kotlinx.android.synthetic.main.fragment_add.*
import java.io.File
import java.io.FileOutputStream


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId;
        if (id == R.id.add_action) {
            export()
            //Toast.makeText(this, "Exportado!", Toast.LENGTH_SHORT).show()
        } else if (id == R.id.add_action2) {
            Import()
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

    fun Import() {

        var importIntent: Intent = Intent(Intent.ACTION_GET_CONTENT)
        importIntent.setType("*/*")
        startActivityForResult(importIntent, 10)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 10) {
            val filePath = data?.data?.path
            importView.setText(filePath)
        }
    }
}