package com.example.todoapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private CardAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<CardItem> mCardItems;

    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Intent intent = new Intent(this, TaskList.class);
        //startActivity(intent);

        Toolbar toolbar = findViewById(R.id.mainToolBar);
        setSupportActionBar(toolbar);

        mCardItems = new ArrayList<>();
        mCardItems.add(new CardItem("List 1", new ArrayList<>()));
        mCardItems.add(new CardItem("List 2", new ArrayList<>()));
        mCardItems.add(new CardItem("List 3", new ArrayList<>()));
        mCardItems.add(new CardItem("List 4", new ArrayList<>()));

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new CardAdapter(mCardItems);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new MySwipeHelper(this, mRecyclerView,200) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buffer) {
                buffer.add(new MyButton(MainActivity.this, "", 24, R.drawable.ic_delete, Color.parseColor("#FF3c30"),
                        pos -> {
                            mAdapter.deleteItem(pos);
                        })
                );
            }
        });

        mAdapter.notifyDataSetChanged();

        mAdapter.setOnItemClickListener(new CardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                Intent intent = new Intent(MainActivity.this, TaskList.class);
                intent.putExtra("Tasklist", mCardItems.get(position).getTaskItems());

                startActivity(intent);

                //Read the list from the intent:
                ArrayList<TaskItem> items = (ArrayList<TaskItem>) intent.getSerializableExtra("Tasklist");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.add:
                mCardItems.add(new CardItem("New Item Added", new ArrayList<>()));

                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.option:
                Toast.makeText(this, "Options clicked", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
