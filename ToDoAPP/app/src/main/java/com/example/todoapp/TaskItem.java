package com.example.todoapp;

public class TaskItem {
    public String taskString;
    public boolean isCompleted;

    public TaskItem(String str, boolean ck){
        isCompleted = ck;
        taskString = str;
    }
}
