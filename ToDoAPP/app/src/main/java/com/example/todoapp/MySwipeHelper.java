package com.example.todoapp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public abstract class MySwipeHelper extends ItemTouchHelper.SimpleCallback {

    private int buttonWidth;
    private RecyclerView mRecyclerView;
    private GestureDetector gestureDetector;
    private int swipedPosition = -1;
    private float swipeThreshhold = 0.5f;
    private List<MyButton> buttonList;
    private Queue<Integer> removeQueue;
    private  Map<Integer, List<MyButton>> buttonBuffer;
    private String mActivity;

    private GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener(){
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            for(MyButton button : buttonList){
                if(button.OnClick(e.getX(),e.getY())) {
                    break;
                }
            }
            return true;
        }
    };

    private View.OnTouchListener onTouchListener = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if(swipedPosition < 0){
                return false;
            }
            Point point = new Point((int)motionEvent.getRawX(), (int)motionEvent.getRawY());
            RecyclerView.ViewHolder swipeViewHolder = mRecyclerView.findViewHolderForAdapterPosition(swipedPosition);
            View swipedItem = swipeViewHolder.itemView;
            Rect rect = new Rect();
            swipedItem.getGlobalVisibleRect(rect);

            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN ||motionEvent.getAction() == MotionEvent.ACTION_UP ||motionEvent.getAction() == MotionEvent.ACTION_MOVE){
                if(rect.top < point.y && rect.bottom > point.y){
                    gestureDetector.onTouchEvent(motionEvent);
                } else {
                    removeQueue.add(swipedPosition);
                    swipedPosition = -1;
                    recoverSwipedItem();
                }
            }
            return false;
        }


    };

    public MySwipeHelper(Context context, String activity, RecyclerView rView, int buttonWidth) {
            super(0, ItemTouchHelper.LEFT);
            this.mRecyclerView = rView;
            this.buttonList = new ArrayList<>();
            this.gestureDetector = new GestureDetector(context, gestureListener);
            this.mRecyclerView.setOnTouchListener(onTouchListener);
            this.buttonBuffer = new HashMap<>();
            this.buttonWidth = buttonWidth;
            this.mActivity = activity;

            removeQueue = new LinkedList<Integer>(){
                @Override
                public boolean add(Integer o) {
                    if(contains(o))
                        return false;
                    else
                        return super.add(o);
                }
            };

            attachSwipe();
    }

    private void attachSwipe() {
            ItemTouchHelper ith = new ItemTouchHelper(this);
            ith.attachToRecyclerView(mRecyclerView);
    }

    private synchronized void recoverSwipedItem() {
        while(!removeQueue.isEmpty()){
            int pos = removeQueue.poll();
            if(pos > -1){
                mRecyclerView.getAdapter().notifyItemChanged(pos);
                if(mActivity.equals("TaskList")){
                    TaskList.Refresh();
                }else if(mActivity.equals("MainActivity")) {
                    MainActivity.Refresh();
                }
            }
        }
    }

    public class MyButton{
        private Context mContext;
        private String text;
        private int imageResId, textSize, color, pos;
        private RectF clickRegion;
        private MybuttonClickListener listener;
        private Resources resources;

        public MyButton(Context mContext, String text, int textSize, int imageResId, int color, MybuttonClickListener listener) {
            this.mContext = mContext;
            this.text = text;
            this.imageResId = imageResId;
            this.textSize = textSize;
            this.color = color;
            this.listener = listener;
            resources = mContext.getResources();
        }

        public boolean OnClick(float x, float y){
            if(clickRegion != null && clickRegion.contains(x,y)){
                listener.onClick(pos);
                return true;
            }
            return false;
        }

        public void onDraw(Canvas canvas, RectF rectF, int pos){
            Paint p = new Paint();
            p.setColor(color);
            canvas.drawRect(rectF, p);

            p.setColor(Color.WHITE);
            p.setTextSize(textSize);

            Rect r = new Rect();
            float cHeight = rectF.height();
            float cWidth = rectF.width();
            p.setTextAlign(Paint.Align.LEFT);
            p.getTextBounds(text, 0, text.length(), r);
            float x=0, y=0;
            if(imageResId== 0){
                x = cWidth/2f - r.width()/2f - r.left;
                y = cHeight/2f + r.height()/2f - r.bottom;
                canvas.drawText(text, rectF.left + x, rectF.top + y, p);
            } else {
                Drawable d = ContextCompat.getDrawable(mContext, imageResId);
                Bitmap bitmap = drawableToBitmap(d);
                float bw = bitmap.getWidth()/2;
                float bh = bitmap.getHeight()/2;
                canvas.drawBitmap(bitmap, ((rectF.left+rectF.right)/2)-bw, ((rectF.top+rectF.bottom)/2 - bh), p);
            }
            clickRegion = rectF;
            this.pos = pos;
        }
    } // End of MyButton class

    private Bitmap drawableToBitmap(Drawable d) {
        if(d instanceof BitmapDrawable){
            return ((BitmapDrawable)d).getBitmap();
        }
        Bitmap bitmap = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        d.setBounds(0,0, canvas.getWidth(), canvas.getHeight());
        d.draw(canvas);
        return bitmap;
    }

    public float getSwipeThreshhold(RecyclerViewAdapter.ViewHolder viewHolder){
        return swipeThreshhold;
    }



    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int pos = viewHolder.getAdapterPosition();
        if(swipedPosition != pos){
            removeQueue.add(swipedPosition);
        }
        swipedPosition = pos;
        if(buttonBuffer.containsKey(swipedPosition)){
            buttonList = buttonBuffer.get(swipedPosition);
        } else {
            buttonList.clear();
        }
        buttonBuffer.clear();
        swipeThreshhold = 0.5f * buttonList.size() * buttonWidth;
        recoverSwipedItem();
        if(mActivity.equals("TaskList")){
            RecyclerViewAdapter.ViewHolder holder = ( RecyclerViewAdapter.ViewHolder) viewHolder;
            holder.text.setTextColor(Color.parseColor("#ebebeb"));
        }else if(mActivity.equals("MainActivity")) {
            CardAdapter.CardViewHolder holder = (CardAdapter.CardViewHolder) viewHolder;
            holder.mTextView.setTextColor(Color.WHITE);
        }
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        if(mActivity.equals("TaskList")){
            RecyclerViewAdapter.ViewHolder holder = ( RecyclerViewAdapter.ViewHolder) viewHolder;
            holder.text.setTextColor(Color.BLACK);
        }else if(mActivity.equals("MainActivity")) {
            CardAdapter.CardViewHolder holder = (CardAdapter.CardViewHolder) viewHolder;
            holder.mTextView.setTextColor(Color.BLACK);
        }
    }

    @Override
    public float getSwipeEscapeVelocity(float defaultValue) {
        return 0.1f*defaultValue;
    }

    @Override
    public float getSwipeVelocityThreshold(float defaultValue) {
        return 0.5f*defaultValue;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
       int pos = viewHolder.getAdapterPosition();
       float transitionX = dX;
       View itemView = viewHolder.itemView;

       if(pos < 0){
           swipedPosition = pos;
           return;
       }
       if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
           if(dX < 0){
               List<MyButton> buffer = new ArrayList<>();
               if(!buttonBuffer.containsKey(pos)){
                   instantiateMyButton(viewHolder,buffer);
                   buttonBuffer.put(pos, buffer);
               } else {
                   buffer = buttonBuffer.get(pos);
               }
               transitionX = dX * buttonBuffer.size() * buttonWidth / itemView.getWidth();
               drawButton(c, itemView, buffer, pos, transitionX);
           }
       }
       super.onChildDraw(c, recyclerView, viewHolder, transitionX, dY, actionState, isCurrentlyActive);
    }

    private void drawButton(Canvas c, View itemView, List<MyButton> buffer, int pos, float transitionX) {
        float right = itemView.getRight();
        float dButtonWidth = -1 * transitionX / buffer.size();
        for(MyButton button:buffer){
            float left = right - dButtonWidth;
            button.onDraw(c, new RectF(left, itemView.getTop(), right, itemView.getBottom()), pos);
            right = left;
        }
    }

    public abstract void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buffer);


}

