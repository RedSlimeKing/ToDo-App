package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.Collections;
import java.util.List;


public class TaskList extends AppCompatActivity {

    private CardItem mCardItem;
    private EditText mListName;
    private RecyclerViewAdapter mAdapter;
    private InputMethodManager imm;
    private  RecyclerView mRecyclerView;
    private int mPosition;

    private static boolean mHideCompleted;
    private static View decor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        decor = getWindow().getDecorView();

        //Read the list from the intent:
        mCardItem = (CardItem) getIntent().getSerializableExtra("Card");
        mPosition = getIntent().getIntExtra("APosition", 0);

        mListName = findViewById(R.id.title_editText);
        mListName.setText(mCardItem.getTitle());

        // Stop items in scene from being pushed up by keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        mListName.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&  (keyCode == KeyEvent.KEYCODE_ENTER)) {
                mListName.clearFocus();
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                mCardItem.setTitle(mListName.getText().toString());
                return true;
            }
            return false;
        });

        mListName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus == false){
                    mCardItem.setTitle(mListName.getText().toString());
                }
            }
        });

        SwitchCompat hideSwitch = findViewById(R.id.switch_comp);

        mHideCompleted = mCardItem.getmHideCompleted();
        hideSwitch.setChecked(mHideCompleted);

        InitRecyclerView();

        hideSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TaskList.setHideCompleted(isChecked);
                if(mHideCompleted) {
                    for (int pos = 0; pos < mAdapter.mList.size(); pos++) {
                        RecyclerViewAdapter.ViewHolder holder = (RecyclerViewAdapter.ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(pos);
                        if(holder != null){
                            if (holder.box.isChecked()) {
                                holder.hide();
                            }
                        }
                    }
                } else {
                    for (int pos = 0; pos < mAdapter.mList.size(); pos++) {
                        RecyclerViewAdapter.ViewHolder holder = (RecyclerViewAdapter.ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(pos);
                        if(holder != null) holder.show();
                    }

                }
            }
        });

        if(mCardItem.getTaskItems().size() <= 0){
            mCardItem.getTaskItems().add(new TaskItem("",false));
            mAdapter.notifyDataSetChanged();
        }
    }

    private void InitRecyclerView(){
        mRecyclerView = findViewById(R.id.recyclerView2);

        mRecyclerView.setHasFixedSize(true);
        mAdapter = new RecyclerViewAdapter(TaskList.this, mCardItem.getTaskItems(), mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new MySwipeHelper(TaskList.this, "TaskList", mRecyclerView,200) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buffer) {
                buffer.add(new MyButton(TaskList.this, "Delete", 30,R.drawable.ic_delete, Color.parseColor("#ebebeb"),
                        pos -> {
                            if(pos != mCardItem.getTaskItems().size() - 1){
                                mAdapter.deleteItem(pos);
                            }
                        })
                );
                buffer.add(new MyButton(TaskList.this, "Check", 30, R.drawable.ic_check, Color.parseColor("#ebebeb"),
                        pos -> {
                            if(pos != mCardItem.getTaskItems().size() - 1) {
                                mAdapter.toggleCheck(pos);
                            }
                        })
                );
            }
        });
        mAdapter.notifyDataSetChanged();

        ItemTouchHelper iTH = new ItemTouchHelper(simpleCallback);
        iTH.attachToRecyclerView(mRecyclerView);
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            if(fromPosition == recyclerView.getAdapter().getItemCount() - 1){
                return false;
            }

            if(toPosition == recyclerView.getAdapter().getItemCount() - 1){
                toPosition -= 1;
            }


            Collections.swap(mCardItem.getTaskItems(), fromPosition, toPosition);

            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);

            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }

        @Override
        public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
            super.onSelectedChanged(viewHolder, actionState);

            if(actionState == ItemTouchHelper.ACTION_STATE_DRAG){
                if(viewHolder.getAdapterPosition() != mAdapter.getItemCount() - 1){
                    viewHolder.itemView.setBackgroundColor(Color.WHITE);
                }
            }
        }

        @Override
        public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }
    };

    public static boolean getHideCompleted(){ return mHideCompleted; }
    public static void setHideCompleted(boolean hide){ mHideCompleted = hide; }

    public static void Refresh(){
        decor.findViewById(android.R.id.content).invalidate();
    }

    @Override
    public void onStop(){
        super.onStop();
        mCardItem.setmHideCompleted(mHideCompleted);
        mCardItem.setTitle(mListName.getText().toString());
        MainActivity.Save(mPosition, mCardItem);
    }

    @Override
    public void onPause() {
        super.onPause();
        mCardItem.setTitle(mListName.getText().toString());
        mCardItem.setmHideCompleted(mHideCompleted);
        MainActivity.Save(mPosition, mCardItem);
    }

    @Override
    public void onBackPressed() {


        mCardItem.setTitle(mListName.getText().toString());
        mCardItem.setmHideCompleted(mHideCompleted);
        Intent resultIntent = new Intent();
        resultIntent.putExtra("returnCard", mCardItem);
        resultIntent.putExtra("APos", mPosition);
        setResult(1, resultIntent);
        super.onBackPressed();
    }

}