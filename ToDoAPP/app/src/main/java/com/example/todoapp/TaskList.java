package com.example.todoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;


public class TaskList extends AppCompatActivity {

    private CardItem mCardItem;
    private EditText mListName;
    private RecyclerViewAdapter adapter;
    private InputMethodManager imm;
    private  RecyclerView rView;
    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //Read the list from the intent:
        mCardItem = (CardItem) getIntent().getSerializableExtra("Card");
        mPosition = getIntent().getIntExtra("APosition", 0);

        mListName = findViewById(R.id.title_editText);
        mListName.setText(mCardItem.getTitle());

        // Stop items in scene from being pushed up by keyboard
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        /*mListName.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&  (keyCode == KeyEvent.KEYCODE_ENTER)) {
                mListName.clearFocus();
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                return true;
            }
            return false;
        });
        */



        InitRecyclerView();

        if(mCardItem.getTaskItems().size() <= 0){
            mCardItem.getTaskItems().add(new TaskItem("",false));
            adapter.notifyDataSetChanged();
        }
    }

    private void InitRecyclerView(){
        rView = findViewById(R.id.recyclerView2);

        rView.setHasFixedSize(true);
        adapter = new RecyclerViewAdapter(TaskList.this, mCardItem.getTaskItems());
        rView.setAdapter(adapter);
        rView.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new MySwipeHelper(TaskList.this, rView,200) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buffer) {
                buffer.add(new MyButton(TaskList.this, "Delete", 30,R.drawable.ic_delete, Color.parseColor("#FF3c30"),
                        pos -> {
                            if(pos != mCardItem.getTaskItems().size() - 1){
                                adapter.deleteItem(pos);
                            }
                        })
                );
                buffer.add(new MyButton(TaskList.this, "Check", 30, R.drawable.ic_check, Color.parseColor("#FF9502"),
                        pos -> adapter.toggleCheck(pos))
                );
            }
        });
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStop(){
        super.onStop();

    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("returnCard", mCardItem);
        resultIntent.putExtra("APos", mPosition);
        setResult(1, resultIntent);
        super.onBackPressed();
    }
}