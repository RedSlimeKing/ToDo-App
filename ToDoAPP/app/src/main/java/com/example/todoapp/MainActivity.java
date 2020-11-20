package com.example.todoapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private static CardAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static ArrayList<CardItem> mCardItems;
    private static Context mContext;
    private TextView textView;
    private Button addButton;
    private static View decor;

    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        decor = getWindow().getDecorView();

        textView = findViewById(R.id.titleText);
        textView.setText("Todo");
        addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(view -> {
            mCardItems.add(new CardItem("New Item Added", new ArrayList<>()));
            mAdapter.notifyDataSetChanged();

            //start activity after creating new
            int position = mAdapter.getItemCount() - 1;
            LoadCard(position);
        });

        mContext = this;

        mCardItems = FileHelper.LoadTask(this);

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new CardAdapter(mCardItems, MainActivity.this);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new MySwipeHelper(MainActivity.this, "MainActivity", mRecyclerView,200) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buffer) {
                buffer.add(new MyButton(MainActivity.this, "Delete", 30, R.drawable.ic_delete, Color.parseColor("#ebebeb"),
                        pos -> {
                            if(pos != mCardItems.size() - 1){
                                mAdapter.deleteItem(pos);
                            }
                        })
                );
            }
        });

        mAdapter.notifyDataSetChanged();

        mAdapter.setOnItemClickListener(position -> LoadCard(position));

        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL) {
            @Override
            public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
                // Do not draw the divider
            }
        });

        ItemTouchHelper iTH = new ItemTouchHelper(simpleCallback);
        iTH.attachToRecyclerView(mRecyclerView);
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            Collections.swap(mCardItems, fromPosition, toPosition);

            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);

            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };

    public static void Refresh(){
        decor.findViewById(android.R.id.content).invalidate();
    }

    public static void Save(int position, CardItem ci){
        mCardItems.set(position, ci);
        mAdapter.notifyDataSetChanged();
        FileHelper.WriteData(mContext, mCardItems);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FileHelper.WriteData(this, mCardItems);
    }

    public void LoadCard(int position){
        CardItem item = mCardItems.get(position);
        Intent intent = new Intent(this, TaskList.class);
        intent.putExtra("Card", item);
        intent.putExtra("APosition", position);

        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1){
            if(resultCode == 1){
                CardItem ci = (CardItem) data.getSerializableExtra("returnCard");
                int Pos = data.getIntExtra("APos", 0);
                mCardItems.set(Pos, ci);
                mAdapter.notifyDataSetChanged();
            }
        }
    }
}
