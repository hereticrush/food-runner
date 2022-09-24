package com.example.food_notes.ui.adapters;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public interface RecyclerViewItemClickListener {

    void onItemClick(int position);

    void onLongItemClick(int position);
}
