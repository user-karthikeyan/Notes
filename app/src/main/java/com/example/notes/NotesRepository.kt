package com.example.notes
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    fun getAllNotesStream(): Flow<List<Notes>>
    
    fun getNoteStream(id: Int): Flow<Notes>

    fun getSearchNotes(pattern:String): Flow<List<Notes>>

    suspend fun insertNote(item: Notes)

    suspend fun deleteNote(item: Notes)

    suspend fun updateNote(item: Notes)
}


class OfflineNotesRepository(private val itemDao: NotesDao) : NotesRepository {
    override fun getAllNotesStream(): Flow<List<Notes>> = itemDao.getAllNotes()

    override fun getNoteStream(id: Int): Flow<Notes> = itemDao.getNotes(id)

    override fun getSearchNotes(pattern: String): Flow<List<Notes>> = itemDao.searchNotes(pattern)

    override suspend fun insertNote(item: Notes) = itemDao.insert(item)

    override suspend fun deleteNote(item: Notes) = itemDao.delete(item)

    override suspend fun updateNote(item: Notes) = itemDao.update(item)
}
