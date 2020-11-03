package com.example.todoapp;

import android.content.Context;
import android.hardware.input.InputManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
    private ArrayList<CardItem> mCardItems;
    private OnItemClickListener mListener;
    private InputMethodManager imm;
    private Context mContext;
    public interface OnItemClickListener{
        void onItemClick(int postion);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public CardAdapter(ArrayList<CardItem> list, Context context){
        mCardItems = list;
        mContext = context;
        imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
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

        holder.mTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mListener.onItemClick(position);
            }
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
        public TextView mTextView;
        public CardViewHolder(View itemView, OnItemClickListener listener){
            super(itemView);

            mTextView = itemView.findViewById(R.id.list_title);
            mTextView.setHint("List name");

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            mTextView.setOnKeyListener((v, keyCode, event) -> {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&  (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    int position = getAdapterPosition();
                    mCardItems.get(position).setTitle(mTextView.getText().toString());

                    mTextView.clearFocus();

                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                    notifyDataSetChanged();
                    return true;
                }
                return false;
            });

        }
    }
}
