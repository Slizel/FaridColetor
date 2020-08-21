package faridnet.com.faridcoletor.Data.InventarioData

import androidx.lifecycle.LiveData
import faridnet.com.faridcoletor.Data.ProdutosData.Produtos

class InventarioRepository (private val inventarioDao: InventarioDao) {

    val readAllData: LiveData<List<Produtos>> = inventarioDao.readAllData()

    suspend fun addInventario(inventario: Inventario){
        inventarioDao.addInventario(inventario)
    }

    suspend fun deleteAllInventarios(){
        inventarioDao.deleteAllInventarios()
    }

    suspend fun deleteProdutos(inventario: Inventario){
        inventarioDao.deleteInventario(inventario)
    }

}