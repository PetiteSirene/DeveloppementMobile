package com.todoarielthibault.todo.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.todoarielthibault.todo.detail.ui.theme.TodoArielThibaultTheme
import com.todoarielthibault.todo.model.Task
import java.util.*

class DetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val initialTask = intent.getSerializableExtra("taskToEdit") as? Task
            ?: Task(id = UUID.randomUUID().toString(), title = "", description = "")
        setContent {
            TodoArielThibaultTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    Detail("Android", onValidate = { newTask ->
                        intent.putExtra("task", newTask)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }, initialTask = initialTask)
                }
            }
        }
    }
}

@Composable
fun Detail(name: String, onValidate: (Task) -> Unit, initialTask: Task) {
    var task by remember { mutableStateOf(initialTask) }


    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Task Detail",
            style = MaterialTheme.typography.headlineSmall
        )
        OutlinedTextField(
            value = task.title,
            onValueChange = { task = task.copy(title = it) },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = task.description,
            onValueChange = { task = task.copy(description = it) },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                onValidate(task)
            }
        ) {
            Text("Validate")
        }

    }
}


