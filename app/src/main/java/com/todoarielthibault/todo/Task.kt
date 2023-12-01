// Dans un nouveau fichier, par exemple, Task.kt
package com.todoarielthibault.todo.model

data class Task(
    val id: String,
    val title: String,
    val description: String = "Default Description"
)
