package com.example.notes
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Notes")
data class Notes(
    @PrimaryKey(autoGenerate = true)
    val id:Int = 0,
    val name:String,
    val content:String
)

