package faridnet.com.faridcoletor.Model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "product_table")
data class Produtos(
    @PrimaryKey
    val codBarras: String,
    val produtoId: Int,
    val descricao: String

): Parcelable {

    fun getprodutoId(): Int {
        return if (this.produtoId == null) return 0
        else return this.produtoId
    }
}

