package faridnet.com.faridcoletor.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import faridnet.com.faridcoletor.Data.ContagensDao
import faridnet.com.faridcoletor.Data.ProdutosDao
import faridnet.com.faridcoletor.Model.Contagens
import faridnet.com.faridcoletor.Model.Produtos

class ProdutosRepository(private val produtosDao: ProdutosDao) {

    val readAllData: LiveData<List<Produtos>> = produtosDao.readAllData()

    suspend fun addProduto(produtos: Produtos){
        produtosDao.addProduto(produtos)
    }

    suspend fun deleteAllProdutos(){
        produtosDao.deleteAllProdutos()
    }

    suspend fun deleteProdutos(produtos: Produtos){
        produtosDao.deleteProdutos(produtos)
    }

    suspend fun updateProdutos(produtos: Produtos){
        produtosDao.updateProdutos(produtos)
    }


    suspend fun loadProductByCodBarra(codBarras: String): Produtos{
        return produtosDao.loadProductByCodBarra(codBarras)
    }

//    fun loadProductByCodBarra(codBarras: String): LiveData<Produtos>{
//        return produtosDao.loadProductByCodBarra(codBarras)
//    }

}