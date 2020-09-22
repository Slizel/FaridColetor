package faridnet.com.faridcoletor.Viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import faridnet.com.faridcoletor.Model.Contagens
import faridnet.com.faridcoletor.Repository.ContagensRepository
import faridnet.com.faridcoletor.Data.AppDatabase
import faridnet.com.faridcoletor.Model.JoinContagemProduto
import faridnet.com.faridcoletor.Model.Produtos
import faridnet.com.faridcoletor.Repository.ProdutosRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {

    val Cont_readAllData: LiveData<List<Contagens>>
    private val Cont_repository: ContagensRepository

    val Prod_readAllData: LiveData<List<Produtos>>
    private val Prod_repository: ProdutosRepository

    val Cont_readAllDatajoinContagemProduto: LiveData<List<JoinContagemProduto>>
    private val contJoin_repository: ContagensRepository


    // Create a LiveData with a String
    val currentProduto: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    init {
        val contagensDao = AppDatabase.getDatabase(application).contagensDao()
        Cont_repository = ContagensRepository(contagensDao)
        Cont_readAllData = Cont_repository.readAllData
    }

    init {
        val contagensDao = AppDatabase.getDatabase(application).contagensDao()
        contJoin_repository = ContagensRepository(contagensDao)
        Cont_readAllDatajoinContagemProduto = contJoin_repository.readAllDatajoinContagemProduto
    }


    init {
        val produtosDao = AppDatabase.getDatabase(application).produtosDao()

        Prod_repository = ProdutosRepository(produtosDao)
        Prod_readAllData = Prod_repository.readAllData
    }


    fun readAllDatajoinContagemProduto(joinContagemProduto: JoinContagemProduto){
        viewModelScope.launch(Dispatchers.IO) {
            contJoin_repository.readAllDatajoinContagemProduto
        }

    }


    fun addContagens(contagens: Contagens) {
        viewModelScope.launch(Dispatchers.IO) {
            Cont_repository.addContagem(contagens)
        }
    }

    fun addProdutos(produtos: Produtos) {
        viewModelScope.launch(Dispatchers.IO) {
            Prod_repository.addProduto(produtos)
        }
    }

    fun deleteAllContagens() {
        viewModelScope.launch(Dispatchers.IO) {
            Cont_repository.deleteAllContagens()
        }
    }

    fun deleteAllProdutos() {
        viewModelScope.launch(Dispatchers.IO) {
            Prod_repository.deleteAllProdutos()
        }
    }

    fun updateContagens(contagens: Contagens) {
        viewModelScope.launch(Dispatchers.IO) {
            Cont_repository.updateContagens(contagens)
        }
    }

    fun updateProdutos(produtos: Produtos) {
        viewModelScope.launch(Dispatchers.IO) {
            Prod_repository.updateProdutos(produtos)
        }
    }

    suspend fun loadProdutobyCodBarra(codBarras: String): Produtos {

        viewModelScope.launch(Dispatchers.IO) {
            Prod_repository.loadProductByCodBarra(codBarras)
        }

        return Prod_repository.loadProductByCodBarra(codBarras)

    }

//    fun loadProdutobyCodBarra(codBarras: String): LiveData<Produtos> {
//        return Prod_repository.loadProductByCodBarra(codBarras)
//    }

    //    fun deleteContagens(contagens: Contagens){
//        viewModelScope.launch(Dispatchers.IO){
//            Cont_repository.deleteContagens(contagens)
//        }
//    }

}