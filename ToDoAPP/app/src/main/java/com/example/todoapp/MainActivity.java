package com.example.todoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private ArrayList<TaskItem> mList;
    private EditText mListName;
    private RecyclerViewAdapter adapter;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListName = findViewById(R.id.listName);
        mListName.setHint("List name");

        // Stop items in scene from being pushed up by keyboard
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        mList = FileHelper.LoadTask(this);
        mListName.setText(FileHelper.GetListName());
        mListName.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&  (keyCode == KeyEvent.KEYCODE_ENTER)) {
                mListName.clearFocus();
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                return true;
            }
            return false;
        });

        InitRecyclerView();

        if(mList.size() <= 0){
            mListName.setText("");
            mList.add(new TaskItem("",false));
            adapter.notifyDataSetChanged();
        }
    }

    private void InitRecyclerView(){
        RecyclerView rView = findViewById(R.id.recView);
        rView.setHasFixedSize(true);
        adapter = new RecyclerViewAdapter(this, mList, rView);
        rView.setAdapter(adapter);
        rView.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new MySwipeHelper(this, rView,200) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buffer) {
                buffer.add(new MyButton(MainActivity.this, "Delete", 30,R.drawable.ic_delete, Color.parseColor("#FF3c30"),
                        new MybuttonClickListener(){
                            @Override
                            public void onClick(int pos) {
                                if(pos != mList.size()-1){
                                    adapter.deleteItem(pos);
                                }
                            }
                        })
                );
                buffer.add(new MyButton(MainActivity.this, "Check", 30, R.drawable.ic_check, Color.parseColor("#FF9502"),
                        new MybuttonClickListener(){
                            @Override
                            public void onClick(int pos) {
                                adapter.toggleCheck(pos);
                            }
                        })
                );
            }
        });
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStop(){
        super.onStop();
        FileHelper.WriteData(this, mList, mListName.getText().toString());
    }


}