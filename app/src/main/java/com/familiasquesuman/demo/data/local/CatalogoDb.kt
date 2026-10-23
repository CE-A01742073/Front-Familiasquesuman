package com.familiasquesuman.demo.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

// Caché offline del catálogo: una fila por registro, el DTO completo guardado como JSON
@Entity(tableName = "catalogo", primaryKeys = ["tipo", "id"])
data class CatalogoEntity(
    val tipo: String,
    val id: String,
    val ciudad: String?,
    val activo: Boolean,
    val orden: String,
    val json: String
)

@Dao
interface CatalogoDao {
    @Upsert
    suspend fun guardar(filas: List<CatalogoEntity>)

    @Query("SELECT * FROM catalogo WHERE tipo = :tipo AND (ciudad = :ciudad OR ciudad IS NULL) AND activo = 1 ORDER BY orden")
    suspend fun listar(tipo: String, ciudad: String): List<CatalogoEntity>

    @Query("SELECT * FROM catalogo WHERE tipo = :tipo AND id = :id LIMIT 1")
    suspend fun buscar(tipo: String, id: String): CatalogoEntity?

    @Query("SELECT COUNT(*) FROM catalogo WHERE tipo = :tipo AND (ciudad = :ciudad OR ciudad IS NULL)")
    suspend fun contar(tipo: String, ciudad: String): Int

    @Query("DELETE FROM catalogo")
    suspend fun borrarTodo()
}

// Cola de escrituras hechas sin conexión; WorkManager las envía con su Idempotency-Key
@Entity(tableName = "pendientes")
data class PendienteEntity(
    @PrimaryKey val clave: String,
    val tipo: String,
    val payload: String,
    val descripcion: String,
    val creadoEn: Long,
    val intentos: Int = 0
)

@Dao
interface PendientesDao {
    @Insert
    suspend fun agregar(p: PendienteEntity)

    @Query("SELECT * FROM pendientes ORDER BY creadoEn")
    suspend fun todos(): List<PendienteEntity>

    @Query("SELECT * FROM pendientes ORDER BY creadoEn")
    fun observar(): Flow<List<PendienteEntity>>

    @Query("UPDATE pendientes SET intentos = intentos + 1 WHERE clave = :clave")
    suspend fun registrarIntento(clave: String)

    @Query("DELETE FROM pendientes WHERE clave = :clave")
    suspend fun borrar(clave: String)

    @Query("DELETE FROM pendientes")
    suspend fun borrarTodo()
}

@Database(entities = [CatalogoEntity::class, PendienteEntity::class], version = 2, exportSchema = false)
abstract class CatalogoDb : RoomDatabase() {
    abstract fun catalogo(): CatalogoDao
    abstract fun pendientes(): PendientesDao

    companion object {
        fun crear(context: Context): CatalogoDb =
            Room.databaseBuilder(context, CatalogoDb::class.java, "catalogo.db")
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
    }
}

object Tipo {
    const val CENTRO = "centro"
    const val ACTIVIDAD = "actividad"
    const val CAMPANA = "campana"
    const val PROYECTO = "proyecto"
}
