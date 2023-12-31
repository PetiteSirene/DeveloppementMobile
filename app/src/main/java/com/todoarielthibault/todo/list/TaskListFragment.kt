package com.todoarielthibault.todo.list

import TaskListAdapter
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.todoarielthibault.todo.R
import com.todoarielthibault.todo.detail.DetailActivity
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


        val createTask = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = result.data?.getSerializableExtra("task") as Task?
            taskList = taskList + task!!
            adapter.submitList(taskList)
        }

        val floatingActionButton = view.findViewById<FloatingActionButton>(R.id.fabAddTask)
        floatingActionButton.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            createTask.launch(intent);
        }

        val editTask = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = result.data?.getSerializableExtra("task") as Task?
            if(task != null){
                taskList = taskList.map { if(it.id == task.id) task else it }
                adapter.submitList(taskList)
            }

        }



        val imageButtonDelete = view.findViewById<ImageButton>(R.id.imageButtonDelete)
        adapter.onClickDelete = { task ->
            taskList = taskList.filterNot { it.id == task.id }
            adapter.submitList(taskList)
        }


        val imageButtonModify = view.findViewById<ImageButton>(R.id.imageButtonModify)
        adapter.onClickEdit = { task ->
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("taskToEdit", task)
            editTask.launch(intent)
        }

    }

}
