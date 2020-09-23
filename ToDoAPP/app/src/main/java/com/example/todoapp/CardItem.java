package com.example.todoapp;

public class CardItem {
    private String mText1;

    public CardItem(String text1){
        mText1 = text1;
    }

    public String getText1(){
        return mText1;
    }

    public void setText1(String text){ mText1 = text; }
}
