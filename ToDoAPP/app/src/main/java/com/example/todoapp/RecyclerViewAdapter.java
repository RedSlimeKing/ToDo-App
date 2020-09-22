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

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    public ArrayList<TaskItem> mList;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private InputMethodManager imm;

    public RecyclerViewAdapter(Context context, ArrayList<TaskItem> ls, RecyclerView rView){
        mContext = context;
        mList = ls;
        imm = (InputMethodManager) mContext.getSystemService(INPUT_METHOD_SERVICE);
        mRecyclerView = rView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent,false);
        ViewHolder holder = new ViewHolder(view);
        if(holder.text.getText().toString().equals("")){
            holder.box.setVisibility(View.GONE);
        }
        holder.text.setHint("Enter Task");
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TaskItem item = mList.get(position);

        holder.box.setChecked(item.isCompleted);
        holder.text.setText(item.taskString);
        
        if(!item.taskString.equals("")){
            holder.box.setVisibility(View.VISIBLE);
        }

        holder.box.setOnClickListener(view -> {
            mList.get(position).isCompleted = holder.box.isChecked();
            holder.box.clearFocus();
            notifyDataSetChanged();
        });

        holder.text.setOnClickListener(view -> {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        });

        holder.text.setOnKeyListener((v, keyCode, event) -> {
            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&  (keyCode == KeyEvent.KEYCODE_ENTER)) {
                if(!holder.text.getText().toString().equals("")){
                    mList.get(position).taskString = holder.text.getText().toString();
                    holder.text.clearFocus();
                    holder.box.setVisibility(View.VISIBLE);
                    // Create next input
                    if(!mList.get(mList.size()-1).taskString.equals("")){
                        mList.add(new TaskItem("",false));
                    }
                    notifyDataSetChanged();
                }
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                return true;
            }
            return false;
        });

        holder.layout.setOnClickListener(view -> { });
    }

    public void deleteItem(int position) {
        mList.remove(position);
        notifyItemRemoved(position);
    }

    public void toggleCheck(int position) {
        TaskItem item = mList.get(position);
        item.isCompleted = !item.isCompleted;
        notifyDataSetChanged();
        
    }

    @Override
    public int getItemCount() {
        return mList.size();
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
