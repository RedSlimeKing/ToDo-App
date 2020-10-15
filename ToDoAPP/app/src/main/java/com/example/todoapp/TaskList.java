package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.List;


public class TaskList extends AppCompatActivity {

    private CardItem mCardItem;
    private EditText mListName;
    private RecyclerViewAdapter adapter;
    private InputMethodManager imm;
    private  RecyclerView rView;
    private int mPosition;

    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private static boolean mHideCompleted;

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

        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        Button options = (Button) findViewById(R.id.options_button);

        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        MenuItem item = (MenuItem) mNavigationView.getMenu().findItem(R.id.hide_complete);
        SwitchCompat hideSwitch = (SwitchCompat) item.getActionView().findViewById(R.id.switch_comp);

        mHideCompleted = mCardItem.getmHideCompleted();
        hideSwitch.setChecked(mHideCompleted);

        InitRecyclerView();

        hideSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mHideCompleted = isChecked;
                if(mHideCompleted) {
                    for (int pos = 0; pos < adapter.mList.size(); pos++) {
                        RecyclerViewAdapter.ViewHolder holder = (RecyclerViewAdapter.ViewHolder) rView.findViewHolderForAdapterPosition(pos);
                        if (holder.box.isChecked()) {
                            holder.hide();
                        }
                    }
                } else {
                    for (int pos = 0; pos < adapter.mList.size(); pos++) {
                        RecyclerViewAdapter.ViewHolder holder = (RecyclerViewAdapter.ViewHolder) rView.findViewHolderForAdapterPosition(pos);
                        holder.show();
                    }

                }
            }
        });

        if(mCardItem.getTaskItems().size() <= 0){
            mCardItem.getTaskItems().add(new TaskItem("",false));
            adapter.notifyDataSetChanged();
        }
    }

    private void InitRecyclerView(){
        rView = findViewById(R.id.recyclerView2);

        rView.setHasFixedSize(true);
        adapter = new RecyclerViewAdapter(TaskList.this, mCardItem.getTaskItems(), rView);
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
                        pos -> {
                            if(pos != mCardItem.getTaskItems().size() - 1) {
                                adapter.toggleCheck(pos);
                            }
                        })
                );
            }
        });
        adapter.notifyDataSetChanged();
    }

    public static boolean getHideCompleted(){ return mHideCompleted; }

    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        mCardItem.setmHideCompleted(mHideCompleted);
        Intent resultIntent = new Intent();
        resultIntent.putExtra("returnCard", mCardItem);
        resultIntent.putExtra("APos", mPosition);
        setResult(1, resultIntent);
        super.onBackPressed();
    }
}