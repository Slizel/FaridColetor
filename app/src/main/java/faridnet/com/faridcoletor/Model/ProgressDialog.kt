package faridnet.com.faridcoletor.Model

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import faridnet.com.faridcoletor.MainActivity
import faridnet.com.faridcoletor.R
import kotlinx.coroutines.CoroutineScope


class ProgressDialog {

    companion object {
        fun progressDialog(context: MainActivity): Dialog{
            val dialog = Dialog(context)
            val inflate = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null)
            dialog.setContentView(inflate)
            dialog.setCancelable(false)
            dialog.window!!.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT)
            )
            return dialog
        }
    }
}