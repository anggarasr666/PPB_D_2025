package com.example.tasktrackr

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.tasktrackr.ui.theme.TaskTrackrTheme
import java.text.SimpleDateFormat
import java.util.*

// Data model untuk task
data class Task(
    val id: UUID = UUID.randomUUID(),
    val title: String,
    val deadline: Date,
    var isCompleted: Boolean = false
)

// Enum class untuk opsi pengurutan
enum class SortOption {
    DEADLINE,
    STATUS
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaskTrackrTheme {
                TaskApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskApp() {
    // State untuk list task
    val tasks = remember { mutableStateListOf<Task>() }

    // State untuk input task baru
    var newTaskTitle by remember { mutableStateOf("") }
    var selectedDateMillis by remember { mutableStateOf(System.currentTimeMillis()) }

    // State untuk pengurutan
    var sortOption by remember { mutableStateOf(SortOption.DEADLINE) }
    var showSortMenu by remember { mutableStateOf(false) }

    // State untuk dialog input task
    var showDialog by remember { mutableStateOf(false) }

    // Format untuk menampilkan tanggal
    val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy - HH:mm", Locale.getDefault())

    // Context untuk date picker dan time picker
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TaskTrackr") },
                actions = {
                    IconButton(onClick = { showSortMenu = true }) {
                        Icon(Icons.Filled.ArrowDropDown, contentDescription = "Sort")
                    }
                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Sort by Deadline") },
                            onClick = {
                                sortOption = SortOption.DEADLINE
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Sort by Status") },
                            onClick = {
                                sortOption = SortOption.STATUS
                                showSortMenu = false
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Task")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Task list
            val sortedTasks = when (sortOption) {
                SortOption.DEADLINE -> tasks.sortedBy { it.deadline }
                SortOption.STATUS -> tasks.sortedBy { it.isCompleted }
            }

            if (sortedTasks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Belum ada tugas. Tambahkan dengan tombol + di bawah!")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(sortedTasks) { task ->
                        TaskItem(
                            task = task,
                            onTaskStatusChanged = { updatedTask ->
                                val index = tasks.indexOfFirst { it.id == updatedTask.id }
                                if (index != -1) {
                                    tasks[index] = updatedTask
                                }
                            }
                        )
                    }
                }
            }
        }

        // Task input dialog
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Tambah Tugas Baru") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = newTaskTitle,
                            onValueChange = { newTaskTitle = it },
                            label = { Text("Judul Tugas") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Date dan time selector menggunakan picker Android native
                        val selectedDate = Date(selectedDateMillis)

                        Text(
                            text = "Deadline: ${dateFormat.format(selectedDate)}",
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        // Date picker button
                        Button(
                            onClick = {
                                val calendar = Calendar.getInstance()
                                calendar.timeInMillis = selectedDateMillis

                                DatePickerDialog(
                                    context,
                                    { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                                        calendar.set(Calendar.YEAR, year)
                                        calendar.set(Calendar.MONTH, month)
                                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                        selectedDateMillis = calendar.timeInMillis
                                    },
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Pilih Tanggal")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Time picker button
                        Button(
                            onClick = {
                                val calendar = Calendar.getInstance()
                                calendar.timeInMillis = selectedDateMillis

                                TimePickerDialog(
                                    context,
                                    { _: TimePicker, hourOfDay: Int, minute: Int ->
                                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                        calendar.set(Calendar.MINUTE, minute)
                                        selectedDateMillis = calendar.timeInMillis
                                    },
                                    calendar.get(Calendar.HOUR_OF_DAY),
                                    calendar.get(Calendar.MINUTE),
                                    true // 24-hour format
                                ).show()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Pilih Waktu")
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newTaskTitle.isNotBlank()) {
                                val newTask = Task(
                                    title = newTaskTitle,
                                    deadline = Date(selectedDateMillis)
                                )
                                tasks.add(newTask)
                                newTaskTitle = ""
                                selectedDateMillis = System.currentTimeMillis()
                                showDialog = false
                            }
                        }
                    ) {
                        Text("Tambah")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Batal")
                    }
                }
            )
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onTaskStatusChanged: (Task) -> Unit
) {
    val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy - HH:mm", Locale.getDefault())
    val isOverdue = task.deadline.before(Date()) && !task.isCompleted

    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 2.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { isChecked ->
                    onTaskStatusChanged(task.copy(isCompleted = isChecked))
                }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Deadline: ${dateFormat.format(task.deadline)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isOverdue) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}