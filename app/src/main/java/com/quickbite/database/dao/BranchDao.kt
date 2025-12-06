package com.quickbite.database.dao

import androidx.room.*
import com.quickbite.database.entities.BranchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BranchDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBranch(branch: BranchEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBranches(branches: List<BranchEntity>)

    @Query("SELECT * FROM branches WHERE isActive = 1 ORDER BY distance ASC")
    fun getAllBranches(): Flow<List<BranchEntity>>

    @Query("SELECT * FROM branches WHERE id = :branchId")
    suspend fun getBranchById(branchId: String): BranchEntity?

    @Query("DELETE FROM branches")
    suspend fun deleteAllBranches()
}
