package com.example.todoapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
    private ArrayList<String> mTitleList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClick(int postion);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public CardAdapter(ArrayList<String> list){
        mTitleList = list;
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
        String title = mTitleList.get(position);
        holder.mTextView.setText(title);
    }

    @Override
    public int getItemCount() {
        return mTitleList.size();
    }

    public void deleteItem(int position) {
        mTitleList.remove(position);
        notifyItemRemoved(position);
    }

    class CardViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
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
