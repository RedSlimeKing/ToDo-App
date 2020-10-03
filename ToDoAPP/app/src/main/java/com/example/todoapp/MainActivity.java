package com.example.todoapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
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

        mCardItems = FileHelper.LoadTask(this);

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new CardAdapter(mCardItems, MainActivity.this);

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
                CardItem item = mCardItems.get(position);
                Intent intent = new Intent(MainActivity.this, TaskList.class);
                intent.putExtra("Card", item);
                intent.putExtra("APosition", position);

                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        FileHelper.WriteData(this, mCardItems);
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
