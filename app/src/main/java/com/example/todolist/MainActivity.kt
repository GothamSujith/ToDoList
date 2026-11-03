package com.example.todolist

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.UUID

class MainActivity : Activity() {

    private lateinit var recyclerTasks: RecyclerView
    private lateinit var txtTotal: TextView
    private lateinit var txtPending: TextView
    private lateinit var txtCompleted: TextView
    private lateinit var txtEmpty: TextView

    private lateinit var adapter: TaskAdapter
    private lateinit var storage: SharedPrefManager

    private val tasks = mutableListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        recyclerTasks = findViewById(R.id.recyclerTasks)
        txtTotal = findViewById(R.id.txtTotal)
        txtPending = findViewById(R.id.txtPending)
        txtCompleted = findViewById(R.id.txtCompleted)
        txtEmpty = findViewById(R.id.txtEmpty)

        storage = SharedPrefManager(this)

        tasks.addAll(storage.getTasks())

        adapter = TaskAdapter(
            tasks,
            onChanged = {
                saveAndRefresh()
            },
            onEdit = { task ->
                showTaskDialog(task)
            },
            onDelete = { task ->
                deleteTask(task)
            }
        )

        recyclerTasks.layoutManager =
            LinearLayoutManager(this)

        recyclerTasks.adapter = adapter

        findViewById<Button>(R.id.btnAddTask)
            .setOnClickListener {
                showTaskDialog()
            }

        updateTaskCount()
    }

    private fun showTaskDialog(task: Task? = null) {

        val input = EditText(this)
        input.hint = "Enter your task"
        input.setSingleLine(true)

        if (task != null) {
            input.setText(task.title)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle(
                if (task == null) "Add New Task"
                else "Edit Task"
            )
            .setView(input)
            .setPositiveButton("Save", null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener {

                    val title = input.text.toString().trim()

                    if (title.isEmpty()) {

                        input.error = "Enter a task"

                    } else {

                        if (task == null) {

                            tasks.add(
                                Task(
                                    id = UUID.randomUUID().toString(),
                                    title = title
                                )
                            )

                        } else {

                            task.title = title
                        }

                        saveAndRefresh()
                        dialog.dismiss()
                    }
                }
        }

        dialog.show()
    }

    private fun deleteTask(task: Task) {

        AlertDialog.Builder(this)
            .setTitle("Delete Task")
            .setMessage("Are you sure you want to delete this task?")
            .setPositiveButton("Delete") { _, _ ->

                tasks.remove(task)
                saveAndRefresh()

                Toast.makeText(
                    this,
                    "Task deleted",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun saveAndRefresh() {

        storage.saveTasks(tasks)
        adapter.notifyDataSetChanged()
        updateTaskCount()
    }

    private fun updateTaskCount() {

        val total = tasks.size
        val completed = tasks.count { it.isCompleted }
        val pending = total - completed

        txtTotal.text = total.toString()
        txtPending.text = "Pending: $pending"
        txtCompleted.text = "Completed: $completed"

        txtEmpty.visibility =
            if (total == 0) View.VISIBLE else View.GONE

        recyclerTasks.visibility =
            if (total == 0) View.GONE else View.VISIBLE
    }
}
