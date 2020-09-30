package com.example.todoapp;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
    private ArrayList<CardItem> mCardItems;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClick(int postion);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public CardAdapter(ArrayList<CardItem> list){
        mCardItems = list;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent,false);
        CardViewHolder cvh = new CardViewHolder(v, mListener);
        return cvh;
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        CardItem item = mCardItems.get(position);
        String title = item.getTitle();
        holder.mTextView.setText(title);

        holder.mTextView.setOnKeyListener((v, keyCode, event) -> {
            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&  (keyCode == KeyEvent.KEYCODE_ENTER)) {
                item.setTitle(holder.mTextView.getText().toString());
                notifyDataSetChanged();
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return mCardItems.size();
    }

    public void deleteItem(int position) {
        mCardItems.remove(position);
        notifyItemRemoved(position);
    }

    class CardViewHolder extends RecyclerView.ViewHolder {
        public EditText mTextView;
        public CardViewHolder(View itemView, OnItemClickListener listener){
            super(itemView);

            mTextView = itemView.findViewById(R.id.list_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        int postion = getAdapterPosition();
                        if(postion != RecyclerView.NO_POSITION){
                            listener.onItemClick(postion);
                        }
                    }
                }
            });
        }
    }
}
