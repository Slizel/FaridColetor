package faridnet.com.faridcoletor.Data.ProdutosData

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_table")
data class Produtos(
    val codBarras: String,
    @PrimaryKey(autoGenerate = true)
    val produtoId: Int,
    val descricao: String

)