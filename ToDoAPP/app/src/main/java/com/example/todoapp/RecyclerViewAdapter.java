package com.example.todoapp;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    public ArrayList<TaskItem> mList;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private InputMethodManager imm;

    public RecyclerViewAdapter(Context context, ArrayList<TaskItem> ls){
        mContext = context;
        mList = ls;
        imm = (InputMethodManager) mContext.getSystemService(INPUT_METHOD_SERVICE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_layout, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TaskItem item = mList.get(position);
        if(item.taskString.equals("")){
            holder.box.setVisibility(View.GONE);
            holder.text.setHint("Enter Task");
        }
        holder.box.setChecked(item.isCompleted);
        holder.box.setOnClickListener(view -> {
            mList.get(position).isCompleted = holder.box.isChecked();
        });

        holder.text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.text.requestFocus();
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY); // Open Keyboard
            }
        });

        holder.text.setOnKeyListener((v, keyCode, event) -> {
            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&  (keyCode == KeyEvent.KEYCODE_ENTER)) {
                if(!holder.text.getText().equals("")){
                    item.taskString = holder.text.getText().toString();
                    holder.text.clearFocus();
                    holder.box.setVisibility(View.VISIBLE);
                    // Create next input
                    mList.add(new TaskItem("",false));
                    notifyDataSetChanged();
                }
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                return true;
            }
            return false;
        });

        holder.text.setText(item.taskString);

        holder.layout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

            }
        });
    }
    public void deleteItem(int position) {
        mList.remove(position);
        notifyItemRemoved(position);
    }

    public void toggleCheck(int position) {
        mList.get(position).isCompleted = !mList.get(position).isCompleted;
        ViewHolder holder = (ViewHolder)mRecyclerView.findViewHolderForAdapterPosition(position);
        holder.box.setVisibility(View.VISIBLE);
        notifyDataSetChanged();
        
    }

    @Override
    public int getItemCount() {
        int size = mList.size();
        return size;
    }

    public Context getContext(){
        return mContext;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public CheckBox box;
        public EditText text;
        public RelativeLayout layout;
        public ViewHolder(View itemView){
            super(itemView);
            box = itemView.findViewById(R.id.checkBox);
            text = itemView.findViewById(R.id.textView);
            layout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
