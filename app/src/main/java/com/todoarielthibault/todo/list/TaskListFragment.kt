package com.todoarielthibault.todo.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.todoarielthibault.todo.R

class TaskListFragment : Fragment() {

    private var taskList = listOf("Task 1", "Task 2", "Task 3")
    private val adapter = TaskListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_task_list, container, false)
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerView)
        adapter.currentList = taskList
        recyclerView.adapter = adapter
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Vous pouvez également mettre ce code dans onCreateView si vous le préférez
    }
}
