package com.example.todoapp;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private CardAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Intent intent = new Intent(this, TaskList.class);
        //startActivity(intent);

        ArrayList<CardItem> cardList = new ArrayList<>();
        cardList.add(new CardItem("Hello"));
        cardList.add(new CardItem("World"));
        cardList.add(new CardItem("Run"));
        cardList.add(new CardItem("Now"));
        cardList.add(new CardItem("The"));
        cardList.add(new CardItem("Child"));
        cardList.add(new CardItem("Fell"));
        cardList.add(new CardItem("Down"));
        cardList.add(new CardItem("Stairs"));

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new CardAdapter(cardList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.notifyDataSetChanged();

        mAdapter.setOnItemClickListener(new CardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int postion) {
                cardList.get(postion).setText1("Clicked!");
                mAdapter.notifyDataSetChanged();
            }
        });
    }

}
