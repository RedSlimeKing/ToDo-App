package com.example.todoapp;

/* File systems */
import android.content.Context;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


public class FileHelper {

    private static final String filename = "StoredTask.txt";
    private static String mListName;

    public static String GetListName(){ return mListName; }

    public static void WriteData(Context context, ArrayList<TaskItem> ls, String listName){
        FileOutputStream fos;
        ArrayList<String> saveData = new ArrayList<>();
        for(TaskItem ti : ls){
            String item = ti.taskString + "[|]" + ti.isCompleted;
            saveData.add(item);
        }
        // Add list name to front of queue to save
        String item = "ListName" + "[|]" + listName;
        saveData.add(0, item);

        try{
            fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(saveData);
            oos.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public static ArrayList<TaskItem> LoadTask(Context context){
        ArrayList<TaskItem> taskItems = new ArrayList<>();
        FileInputStream fis = null;

        try{
            ArrayList<String> items;
            fis = context.openFileInput(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            items = (ArrayList<String>) ois.readObject();

            for(int i = 0; i < items.size(); i++){
                String[] parts = items.get(i).split("[|]");
                String part1 = parts[0].substring(0, parts[0].length() - 1);
                String part2 = parts[1].substring(1);
                //Toast.makeText(context, part1, Toast.LENGTH_SHORT).show();
                if(part1.equals("ListName")){
                    mListName = part2;
                }
                else {
                    taskItems.add(new TaskItem(part1, Boolean.parseBoolean(part2)));
                }
            }
        } catch(IOException | ClassNotFoundException e){
            e.printStackTrace();
        } finally{
            if(fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return taskItems;
    }
}
