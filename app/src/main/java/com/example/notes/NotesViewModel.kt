package com.example.notes

import android.app.Application
import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update

object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer for HomeViewModel
        initializer {
            NotesViewModel(notesApplication().container.notesRepository)
        }
    }
}


val colors = listOf(
    Color(66, 249, 205),
    Color(135, 206, 250),
    Color(144, 238, 144),
    Color(64, 224, 208),
    Color(135, 206, 235),
    Color(152, 255, 152)
)


fun CreationExtras.notesApplication(): NotesApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as NotesApplication)


data class HomeUIState(
    val name:String = "",
    val deleteDialog:Boolean = false,
    val dialogOpen:Boolean = false,
    val selectedNote: Flow<Notes> = emptyFlow(),
    val pendingDelete:Notes? = null,
    val content: String = "",
    val query: MutableStateFlow<String> = MutableStateFlow(""),
    val isActive:Boolean = false,
    val result:Flow<List<Notes>> = emptyFlow(),
    val color: Color = colors[0]
)

class NotesApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}

enum class Routes{
    Home,
    Note
}

interface AppContainer {
    val notesRepository: NotesRepository
}


class AppDataContainer(private val context: Context) : AppContainer {
    override val notesRepository: NotesRepository by lazy {
        OfflineNotesRepository(NotesDatabase.getDatabase(context).notesDao())
    }
}

class NotesViewModel(private val notesRepository: NotesRepository):ViewModel() {
    private val _uiState = MutableStateFlow(HomeUIState())

    val uiState = _uiState.asStateFlow()


    //Function that updates the updated name user entered in the input field in Dialog

    fun updateNewNoteName(name:String){
        _uiState.update {
            it.copy(name = name)
        }
    }


    //Function that inserts new note in the room database

    suspend fun create(){
        if(_uiState.value.name != ""){
            notesRepository.insertNote(Notes(name = _uiState.value.name, content = ""))
        }
        close()
    }

    //Functions for opening and closing dialog

    fun open(){
        _uiState.update {
            it.copy(dialogOpen = true)
        }
    }

    fun close(){
        _uiState.update {
            it.copy(dialogOpen = false, name = "", deleteDialog = false)
        }
    }


    //Function gets all notes in the database

    fun getNotes(): Flow<List<Notes>> {
        return notesRepository.getAllNotesStream()
    }





    //This function marks the note when user clicks the note

    suspend fun selected(note:Notes){
        _uiState.update {
            _uiState.value.copy(selectedNote = notesRepository.getNoteStream(note.id))
        }

        _uiState.value.selectedNote.collect {
            update(it.content)
        }
    }



    //This function to update state variable content that holds the updated content , database is updated only when users saves

    fun update(content:String){
        _uiState.update {
            _uiState.value.copy(content = content)
        }

    }

    fun clearSearch(){
        _uiState.value.query.value = ""
        _uiState.update {
            _uiState.value.copy(result = flowOf(emptyList()))
        }
    }

    suspend fun updateQuery(query:String){
        _uiState.value.query.value = query
        if(_uiState.value.isActive)
            searchQuery()
    }

    suspend fun search(){
        _uiState.update {
            _uiState.value.copy(isActive = true)
        }
        searchQuery()
    }

    fun back(){
        _uiState.update {
            _uiState.value.copy(isActive = false)
        }
        clearSearch()
    }

    fun changeColor(){
        _uiState.update {
            _uiState.value.copy(color = colors[(colors.indexOf(_uiState.value.color) + 1) % colors.size])
        }
    }

    suspend fun rename(note: Notes){
        notesRepository.updateNote(note)
        close()
    }

    suspend fun updateNote(note: Notes){
        notesRepository.updateNote(note)
        close()
    }
    fun deleteNote(notes: Notes){
        _uiState.update {
            _uiState.value.copy(pendingDelete = notes, deleteDialog = true)
        }
        open()
    }

    suspend fun deleteMarked(){
        notesRepository.deleteNote(uiState.value.pendingDelete!!)
        close()
    }

    @OptIn(FlowPreview::class)
    private suspend fun searchQuery(){
            _uiState.value.query.debounce(timeoutMillis = 500).collectLatest { input ->
                _uiState.update {
                    when(input){
                        "" -> _uiState.value.copy(result = flowOf(emptyList()))
                        else -> _uiState.value.copy(result = notesRepository.getSearchNotes(input))
                    }

                }
            }
    }

}