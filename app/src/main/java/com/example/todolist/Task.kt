package com.example.todolist

import java.util.UUID

data class Task(
    val id: String = UUID.randomUUID().toString(),
    var title: String,
    var isCompleted: Boolean = false
)