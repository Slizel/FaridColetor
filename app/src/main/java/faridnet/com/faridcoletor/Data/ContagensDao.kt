package faridnet.com.faridcoletor.Data

import androidx.lifecycle.LiveData
import androidx.room.*
import faridnet.com.faridcoletor.Model.Contagens
import faridnet.com.faridcoletor.Model.JoinContagemProduto
import faridnet.com.faridcoletor.Model.Produtos

@Dao
interface ContagensDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addContagem(contagens: Contagens)

    @Update
    suspend fun updateContagem(contagens: Contagens)

    // Deleta somente 1 linha
    @Delete
    suspend fun deleteContagens(contagens: Contagens)

    @Query("DELETE FROM contagem_table")
    suspend fun deleteAllContagens()

    @Query("SELECT * FROM contagem_table ORDER BY produtoId ASC")
    fun readAllData(): LiveData<List<Contagens>>

    @Query("SELECT * FROM contagem_table ORDER BY produtoId ASC")
    fun readAllInfo(): List<Contagens>
    //@Query("SELECT contagem_table.produtoId, Produtos.descricao, contagem_table.quantidade, contagem_table.datahora FROM contagem_table join (select distinct produtoId, descricao from product_table) Produtos on Produtos.produtoId = contagem_table.produtoId ORDER BY contagem_table.dataHora ASC")

    @get:Query("SELECT * FROM contagem_table")
    val allContagens: List<Contagens>
    
    @Query("SELECT contagem_table.produtoId, Produtos.descricao, contagem_table.quantidade FROM contagem_table join (select distinct produtoId, descricao from product_table) Produtos on Produtos.produtoId = contagem_table.produtoId ORDER BY 2 ASC")
    fun readAllDatajoinContagemProduto(): LiveData<List<JoinContagemProduto>>

    @Query("SELECT * FROM contagem_table where cast(produtoId as int) = cast(:produtoId as int)")
    suspend fun loadContagens(produtoId: String): Contagens

//    @Query("SELECT * FROM product_table where cast(codBarras as int) = cast(:codBarras as int)")
//    suspend fun loadProductByCodBarra(codBarras: String): Produtos



}