package faridnet.com.faridcoletor.Model

import android.os.Parcelable
import androidx.room.ColumnInfo
import kotlinx.android.parcel.Parcelize

@Parcelize
class JoinContagemProduto(

    val produtoId: Int,
    @ColumnInfo(name = "descricao")
    val produtoDescricao: String,
    @ColumnInfo(name = "quantidade")
    val contagemQuantidade: Float
) : Parcelable