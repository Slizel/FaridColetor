package faridnet.com.faridcoletor.Model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

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

//    override fun toString(): String {
//        return "ID=$produtoId; Qtd='$quantidade;' Data='$dataHora;\n"
//    }


    override fun toString(): String {

        val qtde = quantidade
        //val qtde = BigDecimal(quantidade).setScale(3,RoundingMode.HALF_EVEN)
       // val decimal = BigDecimal(quantidade).setScale(3, RoundingMode.UNNECESSARY)
        val df = DecimalFormat("######,###000000.000")
       // val df = DecimalFormat("######,###")

        val dfCodInternoA = DecimalFormat("0000000000000000")
        val codInterno16 = dfCodInternoA.format(produtoId)

        val dfCodInternoB = DecimalFormat("00000000")
        val codInterno8 = dfCodInternoB.format(produtoId)

        var retorno = "00000001001001${codInterno16}${codInterno8}${df.format(qtde)}\n"

        retorno = retorno.replace(".", "")

        return retorno
    }
}