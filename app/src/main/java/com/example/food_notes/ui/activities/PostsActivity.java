package com.example.food_notes.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import com.example.food_notes.R;
import com.example.food_notes.databinding.ActivityPostsBinding;
import com.example.food_notes.ui.adapters.FoodPostRecyclerViewAdapter;
import com.example.food_notes.ui.view.FoodPostViewModel;

public class PostsActivity extends AppCompatActivity {

    private ActivityPostsBinding binding;

    protected RecyclerView mRecyclerView;
    protected FoodPostRecyclerViewAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected FoodPostViewModel mFoodPostViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setRecyclerViewAdapter();
    }

    public void setRecyclerViewAdapter() {
        mRecyclerView = (RecyclerView) binding.rvPosts;
        mLayoutManager = new LinearLayoutManager(this.getApplicationContext());
        mAdapter = new FoodPostRecyclerViewAdapter(this.getApplicationContext(), mFoodPostViewModel);
        mRecyclerView.setAdapter(mAdapter);
    }
}