package com.example.todoapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
    private InputMethodManager imm;
    private RecyclerView rView;

    public RecyclerViewAdapter(Context context, ArrayList<TaskItem> ls, RecyclerView recyclerView){
        mContext = context;
        mList = ls;
        imm = (InputMethodManager) mContext.getSystemService(INPUT_METHOD_SERVICE);
        rView = recyclerView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TaskItem item = mList.get(position);

        holder.box.setChecked(item.isCompleted);
        holder.text.setText(item.taskString);

        if(!item.taskString.equals("")) {
            holder.text.setAlpha(1.0f);
            holder.box.setAlpha(1.0f);
            holder.box.setVisibility(View.VISIBLE);
        }

        if(item.isCompleted){
            holder.text.setAlpha(0.3f);
            holder.box.setAlpha(0.3f);
        }

        if(item.taskString.equals("") && position == 0){
            holder.text.requestFocus();
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }

        holder.box.setOnClickListener(view -> {
            item.isCompleted = holder.box.isChecked();
            holder.show();
            if(item.isCompleted){
                holder.text.setAlpha(0.3f);
                holder.box.setAlpha(0.3f);
            } else {
                holder.text.setAlpha(1.0f);
                holder.box.setAlpha(1.0f);
            }

            if(TaskList.getHideCompleted()){
                holder.hide();
            }

            notifyItemChanged(position);

            holder.box.clearFocus();
        });

        holder.text.setOnClickListener(view -> {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        });

        holder.text.setOnKeyListener((v, keyCode, event) -> {
            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&  (keyCode == KeyEvent.KEYCODE_ENTER)) {
                if(!holder.text.getText().toString().equals("")){
                    item.taskString = holder.text.getText().toString();
                    holder.box.setVisibility(View.VISIBLE);
                    // Create next input
                    if(!mList.get(mList.size()-1).taskString.equals("")){
                        mList.add(new TaskItem("",false));
                    }
                    notifyItemChanged(position);
                }
                holder.text.clearFocus();
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                return true;
            }
            return false;
        });

        holder.text.setOnFocusChangeListener((view, hasFocus) -> {
            if(!hasFocus){
                item.taskString = holder.text.getText().toString();
            }
        });

        holder.layout.setOnClickListener(view -> { });

        if(TaskList.getHideCompleted() && holder.box.isChecked()){
            holder.hide();
        }
    }

    public void deleteItem(int position) {
        mList.remove(position);
        notifyItemRemoved(position);
    }

    public void toggleCheck(int position) {
        TaskItem item = mList.get(position);
        if(!item.taskString.equals("")){
            item.isCompleted = !item.isCompleted;
        }

        if(TaskList.getHideCompleted()) {
            RecyclerViewAdapter.ViewHolder holder = (RecyclerViewAdapter.ViewHolder) rView.findViewHolderForAdapterPosition(position);
            if (holder != null) {
                holder.hide();
            }
        }
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public CheckBox box;
        public EditText text;
        public RelativeLayout layout;
        public ViewGroup.LayoutParams params;

        public ViewHolder(View itemView){
            super(itemView);
            box = itemView.findViewById(R.id.checkBox);
            text = itemView.findViewById(R.id.textView);
            layout = itemView.findViewById(R.id.parent_layout);
            params = layout.getLayoutParams();

            if(text.getText().toString().equals("")){
                box.setVisibility(View.GONE);
            }
            text.setHint("Enter Task");
        }

        public void hide(){
            box.setVisibility(View.GONE);
            text.setVisibility(View.GONE);
            params.height = 0;
            layout.setLayoutParams(params);
        }

        public void show(){
            text.setVisibility(View.VISIBLE);
            if(text.getText().toString().equals("")){
                box.setVisibility(View.GONE);
            } else {
                box.setVisibility(View.VISIBLE);
            }
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            layout.setLayoutParams(params);
        }
    }
}
