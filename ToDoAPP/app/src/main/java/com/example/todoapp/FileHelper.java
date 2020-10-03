package com.example.todoapp;

/* File systems */
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


public class FileHelper {

    private static final String filename = "StoredTask.txt";

    public static void WriteData(Context context, ArrayList<CardItem> ls){
        FileOutputStream fos;
        ArrayList<String> saveData = new ArrayList<>();
        if(ls.size() <= 0){
            return;
        }
        for(int i = 0; i < ls.size(); i++){
            CardItem ci = ls.get(i);
            String item = "ListName" + "[|]" + ci.getTitle();
            saveData.add(item);
            for(TaskItem ti : ci.getTaskItems()){
                item = ti.taskString + "[|]" + ti.isCompleted;
                saveData.add(item);
            }
        }

        try{
            fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(saveData);
            oos.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public static ArrayList<CardItem> LoadTask(Context context){
        ArrayList<CardItem> cardItems = new ArrayList<>();
        FileInputStream fis = null;

        try{
            ArrayList<String> items;
            fis = context.openFileInput(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            items = (ArrayList<String>) ois.readObject();
            int index = -1;
            for(int i = 0; i < items.size(); i++){
                String[] parts = items.get(i).split("[|]");
                String part1 = "", part2 = "";
                part1 = parts[0].substring(0, parts[0].length() - 1);
                part2 = parts[1].substring(1);


                if(part1.equals("ListName")){
                    index++;
                    cardItems.add(new CardItem(part2, new ArrayList<>()));
                } else {
                    cardItems.get(index).addTaskItem(new TaskItem(part1, Boolean.parseBoolean(part2)));
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
        return cardItems;
    }
}
