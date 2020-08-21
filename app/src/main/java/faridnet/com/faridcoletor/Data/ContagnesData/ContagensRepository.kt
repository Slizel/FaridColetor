package faridnet.com.faridcoletor.Data.ContagnesData

import androidx.lifecycle.LiveData

class ContagensRepository(private val contagensDao: ContagensDao) {

    val readAllData: LiveData<List<Contagens>> = contagensDao.readAllData()

    suspend fun addContagem(contagens: Contagens){
        contagensDao.addContagem(contagens)
    }

    suspend fun deleteAllContagens(){
        contagensDao.deleteAllContagens()
    }

    suspend fun deleteContagens(contagens: Contagens) {
        contagensDao.deleteContagens(contagens)

    }


}