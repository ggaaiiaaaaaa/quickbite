package com.quickbite.database.dao
import androidx.room.*
import com.quickbite.database.entities.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Query("SELECT productId FROM favorites WHERE userId = :userId")
    fun getUserFavorites(userId: String): Flow<List<String>>

    @Query("DELETE FROM favorites WHERE userId = :userId AND productId = :productId")
    suspend fun removeFavorite(userId: String, productId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE userId = :userId AND productId = :productId)")
    fun isFavorite(userId: String, productId: String): Flow<Boolean>
}
