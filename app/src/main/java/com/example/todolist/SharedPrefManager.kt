package com.example.todolist

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

class SharedPrefManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("todo_prefs", Context.MODE_PRIVATE)

    fun getTasks(): List<Task> {
        val jsonString = prefs.getString("key_tasks", null) ?: return emptyList()
        val tasksList = mutableListOf<Task>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                tasksList.add(
                    Task(
                        id = obj.optString("id"),
                        title = obj.optString("title"),
                        isCompleted = obj.optBoolean("isCompleted")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return tasksList
    }

    fun saveTasks(tasks: List<Task>) {
        val jsonArray = JSONArray()
        for (task in tasks) {
            val obj = JSONObject().apply {
                put("id", task.id)
                put("title", task.title)
                put("isCompleted", task.isCompleted)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString("key_tasks", jsonArray.toString()).apply()
    }
}