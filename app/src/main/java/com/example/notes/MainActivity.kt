package com.example.notes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.notes.ui.theme.NotesTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import androidx.core.content.ContextCompat.startActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotesTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize()) {
                    CurrentScreen()
                }
            }
        }
    }
}

@Composable
fun CurrentScreen(notesViewModel: NotesViewModel = viewModel(factory = AppViewModelProvider.Factory)) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Home.name,
        enterTransition = { expandHorizontally(animationSpec = tween(500)) },
        exitTransition = { fadeOut(animationSpec = tween(100)) },
        popEnterTransition = { fadeIn(animationSpec = tween(500)) },
        popExitTransition = { fadeOut(animationSpec = tween(100)) }
    ) {
        composable(
            route = Routes.Home.name,
            enterTransition = { expandHorizontally(animationSpec = tween(500)) },
            exitTransition = { fadeOut(animationSpec = tween(100)) },
            popEnterTransition = { fadeIn(animationSpec = tween(500)) },
            popExitTransition = { fadeOut(animationSpec = tween(100)) }) {
            Home(notesViewModel = notesViewModel, navController = navController)
        }
        composable(
            route = Routes.Note.name,
            enterTransition = { scaleIn(animationSpec = tween(500)) },
            exitTransition = { scaleOut(animationSpec = tween(500)) },
            popEnterTransition = { scaleIn(animationSpec = tween(500)) },
            popExitTransition = { fadeOut(animationSpec = tween(500)) }) {
            NoteView(notesViewModel = notesViewModel, navController = navController)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    modifier: Modifier = Modifier,
    notesViewModel: NotesViewModel,
    navController: NavController
) {

    val scope = rememberCoroutineScope()
    val uiState = notesViewModel.uiState.collectAsState()
    val notes = notesViewModel.getNotes().collectAsState(initial = listOf())
    val deleteNote = { note: Notes -> scope.launch { notesViewModel.deleteNote(note) } }
    val openNote = {note:Notes -> navController.navigate(Routes.Note.name);scope.launch { notesViewModel.selected(note) }}

    Scaffold(topBar = {
        Box(
            modifier = modifier
                .background(Color.Black)
                .fillMaxWidth()
        ) {
            SearchBar(colors = SearchBarDefaults.colors(
                containerColor = Color(36, 36, 44, 255),
                dividerColor = Color.LightGray,
                inputFieldColors = SearchBarDefaults.inputFieldColors(
                    focusedTextColor = Color.LightGray,
                    unfocusedTextColor = Color.LightGray,
                    focusedPlaceholderColor = Color.LightGray,
                    unfocusedPlaceholderColor = Color.LightGray,
                    focusedLeadingIconColor = uiState.value.color,
                    unfocusedLeadingIconColor = uiState.value.color
                )
            ),
                modifier = modifier
                    .align(Alignment.Center)
                    .padding(bottom = 10.dp, start = 5.dp, end = 5.dp),
                query = uiState.value.query.collectAsState().value,
                onQueryChange = { scope.launch { notesViewModel.updateQuery(it) } },
                onSearch = { scope.launch { notesViewModel.search() } },
                active = uiState.value.isActive,
                onActiveChange = {},
                placeholder = { Text("Search Notes") },
                leadingIcon = {
                    IconButton(modifier = modifier.padding(end = 15.dp), onClick = {
                        when (uiState.value.isActive) {
                            false -> notesViewModel.changeColor()
                            else -> notesViewModel.back()
                        }
                    }) {
                        when (uiState.value.isActive) {
                            false -> Image(
                                painter = painterResource(id = R.drawable.icon),
                                contentDescription = null,
                                modifier = modifier
                                    .size(30.dp)
                                    .clip(
                                        CircleShape
                                    )
                                    .background(uiState.value.color)
                            )

                            else -> Icon(Icons.Filled.ArrowBack, contentDescription = null)
                        }

                    }
                },
                trailingIcon = {
                    when(uiState.value.isActive){
                        true -> IconButton(onClick = { notesViewModel.clearSearch() }) {
                            Icon(
                                Icons.Filled.Clear,
                                contentDescription = null,
                                tint = uiState.value.color
                            )
                        }
                        else ->IconButton(onClick = { scope.launch { notesViewModel.search() } }) {
                            Icon(
                                Icons.Filled.Search,
                                contentDescription = null,
                                tint = uiState.value.color
                            )
                        }
                    }

                }) {
                Grid(
                    uiState.value.result.collectAsState(initial = mutableListOf()).value,
                    deleteNote = deleteNote,
                    openNote = openNote,
                    notesViewModel = notesViewModel
                )
            }
        }
    }

    ) {
        innerPadding ->
        Box(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.Black)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(5.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                contentPadding = PaddingValues(5.dp), modifier = modifier
                    .fillMaxHeight()
            ) {
                notes.value.forEach {
                    item {
                        NoteCard(it, openNote = openNote, deleteNote = deleteNote, notesViewModel = notesViewModel)
                    }
                }
            }
            FloatingActionButton(
                onClick = { notesViewModel.open() },
                shape = CircleShape,
                containerColor = uiState.value.color,
                contentColor = Color.Black,
                modifier = modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp)
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add a note")
            }
            if(uiState.value.dialogOpen)
                when (uiState.value.deleteDialog) {
                    true -> DeleteConfirm(notesViewModel = notesViewModel)
                    else -> NewNote(notesViewModel = notesViewModel)
                }

        }
    }


}


@Composable
fun Grid(
    result: List<Notes>,
    modifier: Modifier = Modifier,
    deleteNote: (Notes) -> Job,
    openNote: (Notes) -> Job,
    notesViewModel: NotesViewModel
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        contentPadding = PaddingValues(5.dp), modifier = modifier
            .fillMaxSize()
    ) {
        items(result) {
            NoteCard(it, deleteNote, openNote, notesViewModel = notesViewModel)
        }
    }
}


@Composable
fun NewNote(modifier: Modifier = Modifier, notesViewModel: NotesViewModel) {
    val uiState = notesViewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    Dialog(onDismissRequest = { notesViewModel.close() }) {

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(36, 36, 44, 255)),
            modifier = modifier
                .height(160.dp)
                .width(350.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .padding(5.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { notesViewModel.close() }) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = uiState.value.color
                        )
                    }
                    Text("Create Note", fontWeight = FontWeight.SemiBold, color = Color.LightGray)
                    Button(
                        onClick = { scope.launch { notesViewModel.create() } },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = uiState.value.color,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Create")
                    }

                }
                Divider(color = Color.LightGray)
                OutlinedTextField(
                    value = uiState.value.name,
                    onValueChange = { notesViewModel.updateNewNoteName(it) },
                    maxLines = 1,
                    label = { Text("Note Name") },
                    modifier = modifier.padding(vertical = 10.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = uiState.value.color,
                        unfocusedIndicatorColor = Color.LightGray,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedLabelColor = uiState.value.color,
                        unfocusedLabelColor = Color.LightGray,
                        focusedTextColor = Color.LightGray,
                        unfocusedTextColor = Color.LightGray
                    )
                )
            }

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteView(navController: NavController, modifier: Modifier = Modifier, notesViewModel: NotesViewModel) {
    val uiState = notesViewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val state = remember{SnackbarHostState()}
    val note = uiState.value.selectedNote.collectAsState(initial = Notes(name = "", content = ""))


    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = state) {
                Snackbar(snackbarData = it, containerColor = Color(36,36,44), contentColor = uiState.value.color, actionColor = uiState.value.color, dismissActionContentColor = uiState.value.color)
            }
        },
        topBar = {
            CenterAlignedTopAppBar(title = {
                        Text(note.value.name, color = Color.Black, overflow = TextOverflow.Ellipsis, maxLines = 1, modifier = modifier
                            .clip(
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { notesViewModel.open() })
                                           },
                colors = topAppBarColors(
                    containerColor = uiState.value.color,
                    titleContentColor = Color.Black,
                    actionIconContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                ),
                actions = {
                    Share(uiState.value.content, LocalContext.current)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .imePadding()
            .background(Color.Black)) {
            BasicTextField(
                value = uiState.value.content,
                onValueChange = { notesViewModel.update(it) },
                modifier = Modifier
                    .background(Color.Black)
                    .fillMaxSize()
                    .padding(10.dp)
                ,
                textStyle = TextStyle(uiState.value.color, fontSize = 18.sp),
                cursorBrush = Brush.linearGradient(listOf(Color.LightGray, Color.LightGray))
            )
            FloatingActionButton(onClick = { scope.launch {notesViewModel.updateNote(note.value.copy(content = uiState.value.content));state.showSnackbar("Note contents saved successfully", withDismissAction = true)} },
                modifier
                    .align(Alignment.BottomEnd)
                    .padding(15.dp), containerColor = uiState.value.color, contentColor = Color.Black) {
                Icon(Icons.Filled.Save, contentDescription = null)
            }
            if(uiState.value.dialogOpen){
                ChangeName(notesViewModel = notesViewModel)
            }
        }
    }
}


@Composable
fun NoteCard(note: Notes, deleteNote: (Notes) -> Job, openNote: (Notes) -> Job, notesViewModel: NotesViewModel) {
    val uiState = notesViewModel.uiState.collectAsState()
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Black, contentColor = Color.Black),
        shape = CardDefaults.elevatedShape,
        border = BorderStroke(
            2.dp,
            uiState.value.color
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(30, 30)
                )
                .background(uiState.value.color)
        ) {
            IconButton(onClick = { deleteNote(note) }) {
                Icon(Icons.Outlined.RemoveCircleOutline, contentDescription = null)
            }
            Text(note.name, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.fillMaxWidth(0.6f), textAlign = TextAlign.Center)
            IconButton(onClick = {openNote(note)}) {
                Icon(Icons.Outlined.OpenInNew, contentDescription = null)
            }
        }
        Text(
            note.content, color = uiState.value.color, modifier = Modifier
                .clickable { openNote(note) }
                .fillMaxHeight()
                .padding(10.dp)
                .align(Alignment.CenterHorizontally), maxLines = 3, overflow = TextOverflow.Clip
        )
    }
}


@Composable
fun Share(text:String, context:Context) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)

    IconButton(
        onClick = { startActivity(context, shareIntent, null) }) {
        Icon(Icons.Filled.Share, contentDescription = null)

    }

}

@Composable
fun DeleteConfirm(modifier: Modifier = Modifier, notesViewModel: NotesViewModel) {
    val uiState = notesViewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    Dialog(onDismissRequest = { notesViewModel.close() }) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(36, 36, 44, 255)),
            modifier = modifier,
            border = BorderStroke(2.dp, uiState.value.color)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text("Do you want to permanently delete ${uiState.value.pendingDelete!!.name}?", textAlign = TextAlign.Center, color = Color.LightGray)
                Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly){
                    TextButton(
                        onClick = { scope.launch { notesViewModel.close() } },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = uiState.value.color,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Cancel")
                    }
                    TextButton(
                        onClick = { scope.launch { notesViewModel.deleteMarked() } },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = uiState.value.color,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Delete")
                    }
                }
            }
        }
    }

}


@Composable
fun ChangeName(modifier: Modifier = Modifier, notesViewModel: NotesViewModel) {
    val uiState = notesViewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val note = uiState.value.selectedNote.collectAsState(initial = Notes(name="", content = ""))

    Dialog(onDismissRequest = { notesViewModel.close() }) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(36, 36, 44, 255)),
            modifier = modifier
                .height(160.dp)
                .width(400.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .padding(5.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { notesViewModel.close() }) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = uiState.value.color
                        )
                    }
                    Text("Rename Note", fontWeight = FontWeight.SemiBold, color = Color.LightGray)
                    Button(
                        onClick = { scope.launch { notesViewModel.rename(note.value.copy(name = uiState.value.name)) } },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = uiState.value.color,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Rename")
                    }

                }
                Divider(color = Color.LightGray)
                OutlinedTextField(
                    value = uiState.value.name,
                    onValueChange = { notesViewModel.updateNewNoteName(it) },
                    maxLines = 1,
                    label = { Text("New Name") },
                    modifier = modifier.padding(vertical = 10.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = uiState.value.color,
                        unfocusedIndicatorColor = Color.LightGray,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedLabelColor = uiState.value.color,
                        unfocusedLabelColor = Color.LightGray,
                        focusedTextColor = Color.LightGray,
                        unfocusedTextColor = Color.LightGray
                    )
                )
            }
        }
    }

}