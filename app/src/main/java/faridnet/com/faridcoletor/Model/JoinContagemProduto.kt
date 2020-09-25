package faridnet.com.faridcoletor.Model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
class JoinContagemProduto(

    val produtoId: Int,
    @ColumnInfo(name = "descricao")
    val produtoDescricao: String,
    @ColumnInfo(name = "quantidade")
    val contagemQuantidade: Double

): Parcelable