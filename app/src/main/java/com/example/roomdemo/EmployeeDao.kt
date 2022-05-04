package com.example.roomdemo

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeeDao {
    @Insert
    suspend fun insertEmployee(employeeEntity: EmployeeEntity)

    @Update
    suspend fun updateEmployee(employeeEntity: EmployeeEntity)

    @Delete
    suspend fun deleteEmployee(employeeEntity: EmployeeEntity)

    @Query("SELECT * FROM `employee-table`")
    fun getAllEmployees():Flow<List<EmployeeEntity>>

    @Query("SELECT * FROM `EMPLOYEE-TABLE` where id=:id")
    fun fetchOneEmployee(id:Int):Flow<EmployeeEntity>
}