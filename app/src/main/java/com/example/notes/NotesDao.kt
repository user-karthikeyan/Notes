package com.example.notes
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(notes: Notes)

    @Update(entity = Notes::class)
    suspend fun update(notes: Notes)

    @Delete
    suspend fun delete(notes: Notes)

    @Query("SELECT * from notes WHERE id = :id")
    fun getNotes(id: Int): Flow<Notes>

    @Query("SELECT id, name, substr(content, 0, 50) || ' ...' as content from notes")
    fun getAllNotes(): Flow<List<Notes>>

    @Query("SELECT id, name, '...' || substr(content, instr(content, :pattern), 50) || '...' as content from notes WHERE content LIKE '%' || :pattern || '%' UNION ALL SELECT * FROM notes WHERE name LIKE '%' || :pattern || '%' and content NOT LIKE '%' || :pattern || '%'")
    fun searchNotes(pattern:String):Flow<List<Notes>>
}