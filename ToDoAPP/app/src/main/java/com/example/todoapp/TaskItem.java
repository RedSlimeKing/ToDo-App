package com.example.todoapp;

import java.io.Serializable;

public class TaskItem implements Serializable {
    public String taskString;
    public boolean isCompleted;

    public TaskItem(String str, boolean ck){
        isCompleted = ck;
        taskString = str;
    }
}
