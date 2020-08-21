package faridnet.com.faridcoletor.Data.ContagnesData

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "contagem_table"
//    ,foreignKeys = [ForeignKey(
//        entity = Produtos::class,
//        parentColumns =["produtoId"],
//        childColumns = ["produtoId"] )
//    ]
)

data class Contagens(

    @PrimaryKey(autoGenerate = true)
    val produtoId: Int,
    val quantidade: Int,
    val dataHora: String




) {
    override fun toString(): String {
        return "ID=$produtoId Qtd='$quantidade' Data='$dataHora\n"
    }
}