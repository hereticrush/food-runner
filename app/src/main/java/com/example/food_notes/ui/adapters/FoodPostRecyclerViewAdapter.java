package com.example.food_notes.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.food_notes.R;
import com.example.food_notes.ui.view.factory.FoodPostModelViewFactory;
import com.example.food_notes.ui.view.model.FoodPostViewModel;

//TODO implement foodpost adapter for user main rv
public class FoodPostRecyclerViewAdapter extends RecyclerView.Adapter<FoodPostViewHolder> {

    private final Context mContext;
    private final FoodPostViewModel mFoodPostViewModel;

    public FoodPostRecyclerViewAdapter(Context context, FoodPostViewModel foodPostViewModel) {
        this.mContext = context;
        this.mFoodPostViewModel = foodPostViewModel;
    }

    @NonNull
    @Override
    public FoodPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item, parent, false);
        return new FoodPostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodPostViewHolder holder, final int position) {
    }

    @Override
    public int getItemCount() {
        return 0;
    }

}
