package faridnet.com.faridcoletor.Model

import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat

@Parcelize
@Entity(
    tableName = "contagem_table"
//    ,foreignKeys = [ForeignKey(
//        entity = Produtos::class,
//        parentColumns =["produtoId"],
//        childColumns = ["produtoId"] )
//    ]
)

data class Contagens(

    @PrimaryKey
    val produtoId: Int,
    val quantidade: Double,
    val dataHora: String


) : Parcelable {

    override fun toString(): String {
        return "ID=$produtoId Qtd='$quantidade' Data='$dataHora\n"
    }



}