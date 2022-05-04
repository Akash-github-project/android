package com.example.roomdemo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "employee-table")

data class EmployeeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "name_emp")
    val name:String = "",
    @ColumnInfo(name="email_emp")
    val email:String = ""
)
