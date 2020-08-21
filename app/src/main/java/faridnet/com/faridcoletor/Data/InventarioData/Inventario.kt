package faridnet.com.faridcoletor.Data.InventarioData

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inventrio_table")
data class Inventario(

    @PrimaryKey(autoGenerate = true)
    val inventarioId: Int,
    val numeroInventario: Int,
    val numeroContagem: Int

)



