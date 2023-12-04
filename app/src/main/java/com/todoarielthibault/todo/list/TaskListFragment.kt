package com.todoarielthibault.todo.list

import TaskListAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.todoarielthibault.todo.R
import com.todoarielthibault.todo.model.Task
import java.util.*

class TaskListFragment : Fragment() {

    private var taskList = listOf(
        Task(id = "id_1", title = "Task 1", description = "description 1"),
        Task(id = "id_2", title = "Task 2", description = "description 2"),
        Task(id = "id_3", title = "Task 3", description = "description 3")
    )
    private val adapter = TaskListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        adapter.submitList(taskList)

        return inflater.inflate(R.layout.fragment_task_list, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = adapter

        val floatingActionButton = view.findViewById<FloatingActionButton>(R.id.fabAddTask)
        floatingActionButton.setOnClickListener {
            val newTask = Task(id = UUID.randomUUID().toString(), title = "New task")
            taskList = taskList + newTask
            adapter.submitList(taskList)
        }

        val imageButtonDelete = view.findViewById<ImageButton>(R.id.imageButtonDelete)
        adapter.onClickDelete = { task ->
            taskList = taskList.filterNot { it.id == task.id }
            adapter.submitList(taskList)
        }
    }
}
