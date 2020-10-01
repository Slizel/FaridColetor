package faridnet.com.faridcoletor.Model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "contagem_table")
data class Contagens(

    @PrimaryKey
    val produtoId: Int,
    @ColumnInfo(name = "quantidade")
    val quantidade: Double,
    @ColumnInfo(name = "dataHora")
    val dataHora: String

) : Parcelable {

    override fun toString(): String {
        return "ID=$produtoId; Qtd='$quantidade;' Data='$dataHora;\n"
    }
}