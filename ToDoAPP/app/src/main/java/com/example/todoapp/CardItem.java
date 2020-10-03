package com.example.todoapp;

import java.io.Serializable;
import java.util.ArrayList;

public class CardItem implements Serializable {
    private String mTitle;
    private ArrayList<TaskItem> mTaskItems;

    public CardItem(String title, ArrayList<TaskItem> items){
        mTitle = title;
        mTaskItems = items;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public ArrayList<TaskItem> getTaskItems() {
        return mTaskItems;
    }

    public void setTaskItems(ArrayList<TaskItem> mTaskItems) {
        this.mTaskItems = mTaskItems;
    }

    public void addTaskItem(TaskItem ti){ this.mTaskItems.add(ti);}
}
