package faridnet.com.faridcoletor.Model

import androidx.room.ColumnInfo

class JoinContagemProduto(

    val produtoId: Int,
    @ColumnInfo(name = "descricao")
    val produtoDescricao: String,
    @ColumnInfo(name = "quantidade")
    val contagemQuantidade: Float
)