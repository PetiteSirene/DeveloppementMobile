package com.todoarielthibault.todo.list

import TaskListAdapter
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.todoarielthibault.todo.R
import com.todoarielthibault.todo.data.Api
import com.todoarielthibault.todo.detail.DetailActivity
import com.todoarielthibault.todo.model.Task
import kotlinx.coroutines.launch
import coil.load
import com.todoarielthibault.todo.user.UserActivity
import java.util.*





class TaskListFragment : Fragment() {


    private var taskList = listOf(
        Task(id = "id_1", title = "Task 1", description = "description 1"),
        Task(id = "id_2", title = "Task 2", description = "description 2"),
        Task(id = "id_3", title = "Task 3", description = "description 3")
    )
    private val adapter = TaskListAdapter()
    private val viewModel: TasksListViewModel by viewModels()

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
            if (task != null) {
                viewModel.add(task)
            }
            //taskList = taskList + task!!
            //adapter.submitList(taskList)
        }

        val floatingActionButton = view.findViewById<FloatingActionButton>(R.id.fabAddTask)
        floatingActionButton.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            createTask.launch(intent);
        }

        val editTask = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = result.data?.getSerializableExtra("task") as Task?
            if(task != null){
                viewModel.update(task)
                //taskList = taskList.map { if(it.id == task.id) task else it }
                //adapter.submitList(taskList)
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

        lifecycleScope.launch { // on lance une coroutine car `collect` est `suspend`
            viewModel.tasksStateFlow.collect { newList ->
                // cette lambda est exécutée à chaque fois que la liste est mise à jour dans le VM
                // -> ici, on met à jour la liste dans l'adapter
            }
        }

    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            val user = Api.userWebService.fetchUser().body()!!
            view?.findViewById<TextView>(R.id.UserInfo)?.setText(user.name)

            val userPhotoImageView = view?.findViewById<ImageView>(R.id.UserPhoto)
            userPhotoImageView?.load(user.avatar) {
                error(R.drawable.ic_launcher_background) // Gérer les erreurs de chargement ici
            }

            userPhotoImageView?.setOnClickListener {
                val intent = Intent(context, UserActivity::class.java)
                startActivity(intent)
            }
        }
        viewModel.refresh()
    }

}
