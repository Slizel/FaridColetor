package faridnet.com.faridcoletor.Data.ProdutosData

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ProdutosDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addProduto(produtos: Produtos)

    //    Deleta somente 1 linha
    @Delete
    suspend fun deleteProdutos(produtos: Produtos)

    @Query("DELETE FROM product_table")
    suspend fun deleteAllProdutos()

    @Query("SELECT * FROM product_table ORDER BY produtoId ASC")
    fun readAllData(): LiveData<List<Produtos>>
}